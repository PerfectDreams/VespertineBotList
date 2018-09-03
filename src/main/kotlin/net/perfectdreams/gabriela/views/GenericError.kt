package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.Constants
import org.jooby.Request

object GenericError {
	fun build(req: Request, error: String) = Base.build(req, "Oopsie Woopsie!", null) {
		div {
			id = "content"
			div {
				id = "container"
				div {
					id = "text-container"
					classes += "center-text"
					img(src = "${Constants.WEBSITE_URL}/assets/img/lori_hm.png")
					h1 {
						+ error
					}
					p {
						+ "Tente novamente!"
					}
				}
			}
		}
	}
}