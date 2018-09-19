package net.perfectdreams.gabriela.website.routes

import net.perfectdreams.gabriela.views.SearchResultView
import net.perfectdreams.gabriela.views.SearchView
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
			res.send(SearchResultView().generate(req, res))
		} else {
			res.send(SearchView().generate(req, res))
		}
	}
}