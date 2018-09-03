package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.Constants
import org.jooby.Request

object VoteBotSuccess {
	fun build(req: Request) = Base.build(req, "Wow!", null) {
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