package net.perfectdreams.gabriela.website.routes.bot.configure

import com.mongodb.client.model.Filters
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.views.GenericErrorView
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.GET
import org.jooby.mvc.Path

@Path("/bot/:botId/invite")
class InviteBotController {
	@GET
	fun handle(req: Request, res: Response) {
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId)

		if (botInfo != null && bot != null) {
			val inviteUrl = if (botInfo.inviteUrl.isNullOrBlank()) { "https://discordapp.com/api/oauth2/authorize?client_id=$botId&permissions=0&scope=bot" } else { botInfo.inviteUrl!! }
			res.redirect(Status.FOUND, inviteUrl)
			res.send("")
			return
		}

		res.status(Status.FORBIDDEN)
		res.send(GenericErrorView("whoops").generate(req, res))
	}
}