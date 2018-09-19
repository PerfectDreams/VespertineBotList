package net.perfectdreams.gabriela.website.routes

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.views.TopBotsView
import org.jooby.Request
import org.jooby.Response
import org.jooby.mvc.GET
import org.jooby.mvc.Path

@Path("/page/top/:pageId")
class TopBotsPageController {
	@GET
	fun searchPage(req: Request, res: Response) {
		val pageId = req.param("pageId").value("1").toIntOrNull() ?: 1

		val skip = (pageId - 1) * 48
		val query = org.bson.Document.parse("{ \$addFields: { \"validVotes\": { \$filter: { input: \"\$votes\", as: \"item\", cond: {\$gt: [\"\$\$item.votedAt\", ${System.currentTimeMillis() - 2592000000}]}}}}}")
		val bestBots = gabriela.collection
				.aggregate(
						listOf(
								Aggregates.match(Filters.eq("approved", true)),
								query,
								org.bson.Document("\$addFields", org.bson.Document("length", org.bson.Document("\$size", org.bson.Document("\$ifNull", listOf("\$validVotes", emptyList<Any>()))))),
								Aggregates.sort(Sorts.descending("length")),
								Aggregates.skip(skip),
								Aggregates.limit(48)
						)
				).toMutableList()

		res.send(TopBotsView("Os melhores bots para o Discord", bestBots).generate(req, res))
	}
}