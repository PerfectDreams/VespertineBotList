package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

class SearchView : BaseView() {
	override fun getPageTitle(): String {
		return "Pesquisar"
	}


	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
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