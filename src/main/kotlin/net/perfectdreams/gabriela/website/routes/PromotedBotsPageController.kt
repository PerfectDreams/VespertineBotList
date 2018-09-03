package net.perfectdreams.gabriela.website.routes

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.views.TopBots
import org.jooby.Request
import org.jooby.Response
import org.jooby.mvc.GET
import org.jooby.mvc.Path

@Path("/page/promoted/:pageId")
class PromotedBotsPageController {
	@GET
	fun searchPage(req: Request, res: Response) {
		val pageId = req.param("pageId").value("1").toIntOrNull() ?: 1

		val skip = (pageId - 1) * 48
		val bestBots = gabriela.collection
				.find(
						Filters.eq("approved", true)
				)
				.sort(Sorts.descending("lastBump"))
				.skip(skip)
				.limit(48)
				.toMutableList()

		res.send(TopBots.build(req, res, "Bots recentemente promovidos", bestBots))
	}
}