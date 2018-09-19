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

@Path("/bot/:botId/repository")
class RepositoryBotController {
	@GET
	fun handle(req: Request, res: Response) {
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId)

		if (botInfo != null && bot != null) {
			val repositoryUrl = if (!botInfo.repositoryUrl.isNullOrBlank()) { botInfo.repositoryUrl!! } else {
				res.status(Status.FORBIDDEN)
				res.send(GenericErrorView("whoops").generate(req, res))
				return
			}
			res.redirect(Status.FOUND, repositoryUrl)
			res.send("")
			return
		}

		res.status(Status.FORBIDDEN)
		res.send(GenericErrorView("whoops").generate(req, res))
	}
}