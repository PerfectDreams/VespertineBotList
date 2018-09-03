package net.perfectdreams.gabriela.stylesheet

import kotlinx.css.*
import net.perfectdreams.gabriela.utils.EntryColor
import net.perfectdreams.gabriela.utils.toKotlinCss

object Stylesheet {
	const val WHITNEY_FONT_FAMILY = "Whitney,Helvetica Neue,Helvetica,Arial,sans-serif"
	fun buildStylesheet(): String {
		var styles = CSSBuilder().apply {
			body {
				backgroundColor = Color("rgb(230, 236, 240)")
				backgroundImage = Image("url(http://127.0.0.1:6660/assets/img/website_bg.png?v=6)")
			}

			hr {
				boxSizing = BoxSizing.contentBox
				background = "rgba(0,0,0,.05)"
				border = "0"
				height = 1.px
			}

			"#content" {
				marginLeft = 210.px
				marginRight = 210.px
			}

			"#container" {
				backgroundColor = Color.white
				fontFamily = WHITNEY_FONT_FAMILY
				margin(7.px)
				borderRadius = 7.px

			}

			"#text-container" {
				padding(15.px)
			}

			".center-text" {
				textAlign = TextAlign.center
			}

			".rounded-image" {
				borderRadius = 9999.px
			}

			".topnav" {
				backgroundColor = EntryColor.BLURPLE.secondary.toKotlinCss()
				overflow = Overflow.hidden
				zIndex = 2
				textTransform = TextTransform.uppercase
			}
		}.toString()

		println(styles)
		return styles
	}
}