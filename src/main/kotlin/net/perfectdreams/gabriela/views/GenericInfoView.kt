package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.Constants
import org.jooby.Request
import org.jooby.Response

class GenericInfoView(val info: String) : BaseView() {
	override fun getPageTitle(): String {
		return "Sucesso!"
	}

	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		div {
			id = "content"
			div {
				id = "container"
				div {
					id = "text-container"
					classes += "center-text"
					img(src = "${Constants.WEBSITE_URL}/assets/img/lori_hm.png")
					h1 {
						+ info
					}
				}
			}
		}
	}
}