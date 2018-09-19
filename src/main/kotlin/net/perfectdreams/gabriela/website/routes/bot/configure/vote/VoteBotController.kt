package net.perfectdreams.gabriela.website.routes.bot.configure.vote

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.set
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.experimental.launch
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.models.Vote
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.jsonParser
import net.perfectdreams.gabriela.utils.DiscordUtils
import net.perfectdreams.gabriela.utils.trueIp
import net.perfectdreams.gabriela.utils.urlQueryString
import net.perfectdreams.gabriela.views.GenericErrorView
import net.perfectdreams.gabriela.views.VoteBotSuccessView
import net.perfectdreams.gabriela.views.VoteBotView
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.GET
import org.jooby.mvc.POST
import org.jooby.mvc.Path

@Path("/bot/:botId/vote")
class VoteBotController {
	@GET
	fun showBotVote(req: Request, res: Response) {
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId)
		val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
		val userIdentification = if (optional.isPresent) optional.get() else null

		if (botInfo != null && bot != null) {
			res.send(VoteBotView(userIdentification, bot, botInfo).generate(req, res))
			return
		}

		res.status(Status.FORBIDDEN)
		res.send(GenericErrorView("whoops").generate(req, res))
	}

	@POST
	fun handlePayload(req: Request, res: Response) {
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId)

		if (botInfo != null) {
			val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
			val userIdentification = if (optional.isPresent) optional.get() else return
			val recaptcha = req.param("recaptcha").value()

			val body = HttpRequest.post("https://www.google.com/recaptcha/api/siteverify")
					.contentType("application/x-www-form-urlencoded")
					.send("secret=${gabriela.config.recaptchaSecret}&response=$recaptcha")
					.body()

			val jsonParser = jsonParser.parse(body).obj

			val success = jsonParser["success"].bool

			if (!success) {
				res.status(Status.FORBIDDEN)
				res.send(GenericErrorView("reCAPTCHA inválido!").generate(req, res))
				return
			}

			val canVote = botInfo.canUpvote(userIdentification.id, req.trueIp)
			if (!canVote) {
				res.status(Status.FORBIDDEN)
				res.send(GenericErrorView("Você já votou hoje!").generate(req, res))
				return
			}

			val result = DiscordUtils.verifyAccount(userIdentification, req.trueIp)

			if (!result.canAccess) {
				res.status(Status.FORBIDDEN)
				res.send(GenericErrorView("Sua conta parece ser maliciosa... você está usando proxies?").generate(req, res))
				return
			}

			if (botInfo.webhookUrl != null) {
				// Ao votar, iremos enviar uma webhook em uma task separada
				launch {
					// TODO: Coroutines
					val payload = jsonObject(
							"event" to "upvote",
							"userId" to userIdentification.id
					)

					if (req.urlQueryString.isNotEmpty()) {
						payload["query"] = req.urlQueryString
					}

					// Enviar...
					HttpRequest.post(botInfo.webhookUrl)
							.send(payload.toString())
							.ok()
				}
			}

			val vote = Vote(
					userIdentification.id,
					System.currentTimeMillis(),
					req.trueIp,
					userIdentification.email!!
			)

			gabriela.collection.updateOne(
					Filters.eq("_id", botId),
					Updates.push("votes", vote)
			)

			res.send(VoteBotSuccessView().generate(req, res))
		}
	}
}