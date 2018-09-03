package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

object Search {
	fun build(req: Request, res: Response) = Base.build(req, "Pesquisar", null) {
		div {
			id = "content"
			generateHeader(
					"fas fa-search",
					"Pesquisar",
					"Pesquise em nossa lista de bots o que você deseja!"
			)
			form {
				input(InputType.text, name = "q")
				button(type = ButtonType.submit) {
					+ "Pesquisar"
				}
			}
		}
	}
}