package net.perfectdreams.gabriela.website.routes.bot.configure

import com.mongodb.client.model.Filters
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.views.GenericError
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.GET
import org.jooby.mvc.Path

@Path("/bot/:botId/website")
class WebsiteBotController {
	@GET
	fun handle(req: Request, res: Response) {
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId)

		if (botInfo != null && bot != null) {
			val websiteUrl = if (!botInfo.websiteUrl.isNullOrBlank()) { botInfo.websiteUrl!! } else {
				res.status(Status.FORBIDDEN)
				res.send(GenericError.build(req, "whoops"))
				return
			}
			res.redirect(Status.FOUND, websiteUrl)
			res.send("")
			return
		}

		res.status(Status.FORBIDDEN)
		res.send(GenericError.build(req, "whoops"))
	}
}