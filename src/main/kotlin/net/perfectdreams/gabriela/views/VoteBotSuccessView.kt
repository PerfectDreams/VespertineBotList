package net.perfectdreams.gabriela.views

import kotlinx.css.h1
import kotlinx.css.img
import kotlinx.html.*
import net.perfectdreams.gabriela.utils.Constants
import org.jooby.Request
import org.jooby.Response

class VoteBotSuccessView : BaseView() {
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
						+"Você votou com sucesso! Yay!"
					}
				}
			}
		}
	}
}