package net.perfectdreams.gabriela.website.routes

import net.perfectdreams.gabriela.views.Search
import net.perfectdreams.gabriela.views.SearchResult
import org.jooby.Request
import org.jooby.Response
import org.jooby.mvc.GET
import org.jooby.mvc.Path

@Path("/search")
class SearchController {
	@GET
	fun searchPage(req: Request, res: Response) {
		val query = req.param("q")

		if (query.isSet) {
			res.send(SearchResult.build(req, res))
		} else {
			res.send(Search.build(req, res))
		}
	}
}