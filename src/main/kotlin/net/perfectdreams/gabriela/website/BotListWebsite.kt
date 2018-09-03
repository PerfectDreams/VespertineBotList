package net.perfectdreams.gabriela.website

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.mongodb.client.model.Filters
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth.Companion.jsonParser
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.Constants.gson
import net.perfectdreams.gabriela.utils.encodeToUrl
import net.perfectdreams.gabriela.utils.urlQueryString
import net.perfectdreams.gabriela.views.Dashboard
import net.perfectdreams.gabriela.views.Home
import net.perfectdreams.gabriela.views.NotFoundError
import net.perfectdreams.gabriela.views.ViewBot
import net.perfectdreams.gabriela.website.routes.PromotedBotsPageController
import net.perfectdreams.gabriela.website.routes.SearchController
import net.perfectdreams.gabriela.website.routes.TopBotsPageController
import net.perfectdreams.gabriela.website.routes.api.bot.BotStatsController
import net.perfectdreams.gabriela.website.routes.api.bot.LifetimeBotVotesController
import net.perfectdreams.gabriela.website.routes.api.bot.MonthlyBotVotesController
import net.perfectdreams.gabriela.website.routes.bot.configure.*
import net.perfectdreams.gabriela.website.routes.bot.configure.vote.VoteBotController
import net.perfectdreams.gabriela.website.routes.dashboard.AddBotController
import org.jooby.Kooby
import org.jooby.Status
import org.jooby.mongodb.MongoSessionStore
import org.jooby.mongodb.Mongodb
import java.io.File
import java.util.*

class BotListWebsite(val websiteUrl: String, val frontendFolder: String) : Kooby({
	port(6660) // Porta do website
	use(Mongodb()) // Usar extensão do MongoDB para o Jooby
	session(MongoSessionStore::class.java) // Usar session store para o MongoDB do Jooby

	/* get("/assets/css/style.css") { req, res ->
		res.send(Stylesheet.buildStylesheet())
	} */
	assets("/**", File(frontendFolder, "static/").toPath()).onMissing(0)
	use("*") { req, res, chain ->
		val discordAuth = req.session().get("discordAuth")
		if (discordAuth.isSet) {
			val str = discordAuth.value()
			try {
				val auth = gson.fromJson<TemmieDiscordAuth>(str)
				val userIdentification = auth.getUserIdentification()
				req.set("discordAuth", auth)
				req.set("userIdentification", userIdentification)
			} catch (e: Exception) {
				// Falha na autenticação
				req.session().unset("discordAuth")
			}
		}

		val requiresAuth = req.route().attributes().entries.firstOrNull { it.key == "requiresAuth" }?.value as Boolean?

		if (requiresAuth == true && !req.ifGet<TemmieDiscordAuth>("discordAuth").isPresent) {
			val state = jsonObject(
					"redirectUrl" to Constants.WEBSITE_URL + req.path() + req.urlQueryString
			)
			res.redirect(Constants.AUTHORIZATION_URL + "&state=${Base64.getEncoder().encodeToString(state.toString().toByteArray()).encodeToUrl()}")
			return@use
		}

		chain.next(req, res)
	}
	use(BotStatsController::class.java)
	use(MonthlyBotVotesController::class.java)
	use(LifetimeBotVotesController::class.java)
	use(VoteBotController::class.java)
	use(InviteBotController::class.java)
	use(SupportBotController::class.java)
	use(WebsiteBotController::class.java)
	use(RepositoryBotController::class.java)
	use(TopBotsPageController::class.java)
	use(PromotedBotsPageController::class.java)
	use(SearchController::class.java)
	get("/") { req, res ->
		res.send(Home.build(req, res))
	}
	get("/bot/:botId") { req, res ->
		val botId = req.param("botId")
		val botInfo = gabriela.collection.find(Filters.eq("_id", botId.value())).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId.value())

		if (botInfo != null) {
			if (bot != null) {
				res.send(ViewBot.build(req, botInfo, bot))
				return@get
			} else {
				res.send(NotFoundError.build(req))
				return@get
			}
		}

		res.status(Status.NOT_FOUND)
		res.send(NotFoundError.build(req))
	}
	get("/login") { req, res ->
		// OAuth2 flow
		if (req.param("code").isSet) { // Pedido de autenticação
			val code = req.param("code").value()
			val auth = TemmieDiscordAuth(code, "$WEBSITE_URL/login", gabriela.config.clientId, gabriela.config.clientSecret).apply {
				debug = true
			}
			auth.doTokenExchange()

			req.session().set("discordAuth", gson.toJson(auth))
			req.set("discordAuth", auth)

			if (req.param("state").isSet) {
				val stateString = req.param("state").value()
				val json = jsonParser.parse(String(Base64.getDecoder().decode(stateString)))
				res.redirect(json["redirectUrl"].string)
				return@get
			}
			res.redirect("$WEBSITE_URL/dashboard")
			return@get
		}

		val optional = req.ifGet<TemmieDiscordAuth>("discordAuth")
		val discordAuth = if (optional.isPresent) optional.get() else null
		if (discordAuth == null) {
			// Não logado, vamos pedir autenticação para o usuário
			res.redirect(Constants.AUTHORIZATION_URL)
			return@get
		}

		try {
			discordAuth.getUserIdentification()
		} catch (e: Exception) {
			// Falha na autenticação
			req.session().unset("discordAuth")
			res.redirect(Constants.AUTHORIZATION_URL)
			return@get
		}

		// Autenticado, vamos redirecionar para o dashboard
		res.redirect("$WEBSITE_URL/dashboard")
		return@get
	}
	with {
		get("/dashboard") { req, res ->
			res.send(Dashboard.build(req, res))
		}
		use(AddBotController::class.java)
		use(ConfigureBotController::class.java)
	}.attr("requiresAuth", true)

	err(404) { req, res, err ->
		res.send(NotFoundError.build(req))
	}
})