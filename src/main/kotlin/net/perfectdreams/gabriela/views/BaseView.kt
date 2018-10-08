package net.perfectdreams.gabriela.views

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.EntryColor
import org.jooby.Request
import org.jooby.Response
import java.io.StringWriter
import java.util.*

abstract class BaseView {
	open fun getPageTitle(): String = "Bots, bots, bots!"
	open fun getDescription(): String = "Te ajudando a transformar o seu servidor no Discord em algo incrível!"
	open fun getIcon(): String = "https://bots.perfectdreams.net/assets/img/gabriela.png"
	open fun getEntryColor(): EntryColor {
		return EntryColor.BLURPLE
	}

	abstract fun getContent(req: Request, res: Response): DIV.() -> Unit

	fun generate(req: Request, res: Response) = StringWriter().appendHTML().html {
		head {
			title { + ("${getPageTitle()} • Discord Bots") }
			meta("theme-color", "#7289da")
			meta(name = "viewport", content = "width=device-width, initial-scale=1")
			unsafe {
				raw("""<link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
<link rel="manifest" href="/site.webmanifest">
<link rel="mask-icon" href="/safari-pinned-tab.svg" color="#5bbad5">
<meta name="msapplication-TileColor" content="#00aba9">
""")
			}
			unsafe {
				raw("""<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-53518408-12"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'UA-53518408-12');
</script>
""")
			}
			script(src = "https://www.google.com/recaptcha/api.js") {}
			script(src = "https://code.jquery.com/jquery-3.3.1.min.js") {}
			script(src = "//twemoji.maxcdn.com/2/twemoji.min.js?11.0") {}
			script(src = "$WEBSITE_URL/assets/js/kotlin.js") {}
			script(src = "$WEBSITE_URL/assets/js/VespertineBotListJS.js") {}
			styleLink("https://unpkg.com/purecss@1.0.0/build/pure-min.css")
			styleLink("https://unpkg.com/purecss@1.0.0/build/grids-responsive-min.css")
			styleLink("https://use.fontawesome.com/releases/v5.2.0/css/all.css")
			styleLink("$WEBSITE_URL/assets/css/style.css")
			styleLink("https://rawgit.com/ellekasai/twemoji-awesome/gh-pages/twemoji-awesome.css")
			meta(content = "Discord Bots") { attributes["property"] = "og:site_name" }
			meta(content = getDescription()) { attributes["property"] = "og:description" }
			meta(content = getPageTitle()) { attributes["property"] = "og:title" }
			meta(content = "600") { attributes["property"] = "og:ttl" }
			meta(content = getIcon()) { attributes["property"] = "og:image"}
		}
		body {
			classes += "color-${getEntryColor().name.toLowerCase().replace("_", "-")}"
			div("topnav navbar-fixed-top") {
				id = "myTopnav"
				div("topnavWrapper") {
					div("topnavLeft") {
						a(href = WEBSITE_URL, classes = "bot-title") {
							+ "Bot List"
						}
						a(href = "$WEBSITE_URL/search") {
							i(classes = "fas fa-search")
							+ " Pesquisar"
						}
						a(href = "https://discord.gg/PBAFd3V") {
							i(classes = "fab fa-discord")
							+ " Nosso Servidor"
						}
					}
					val optional = req.ifGet<TemmieDiscordAuth>("discordAuth")
					val discordAuth = if (optional.isPresent) optional.get() else null
					div("topnavRight") {
						div("rightEntries") {
							if (discordAuth != null) {
								a(href = "$WEBSITE_URL/logout") {
									i(classes = "fas fa-sign-out-alt")
								}
								a(href = "$WEBSITE_URL/dashboard") {
									i(classes = "fas fa-robot")
									+" Seus Bots"
								}
							} else {
								a(href = "$WEBSITE_URL/login") {
									i(classes = "fas fa-sign-in-alt")
									+" Login"
								}
							}
						}
						a(href = "javascript:void(0);", classes = "icon") {
							attributes["onclick"] = "myFunction()"
							+ "☰"
						}
					}
				}
			}
			div {
				id = "dummyNavbar"
			}
			div {
				style = "flex: 1;"
				getContent(req, res).invoke(this)
			}
			footer {
				unsafe {
					raw("""
<div style="text-align: center; font-size: 18px; font-weight: 600;">© MrPowerGamerBR &amp; PerfectDreams ${Calendar.getInstance()[Calendar.YEAR]} — Todos os direitos reservados</div>
<div style="text-align: center; font-weight: 600;"><a style="color: hsla(0,0%,100%,.6);" href="https://mrpowergamerbr.com/">https://mrpowergamerbr.com/</a></div>
<hr>
<div style="text-align: center; font-size: 14px; padding-bottom: 8px;">A Loritta não está de forma alguma afiliado ao Discord. Nem deve ser considerada uma empresa endossada pelo Discord.</div>
<div style="text-align: center; font-size: 10px;">Discord ®/™ &amp; © 2015-<script>document.write(new Date().getFullYear());</script>2018 Discord Inc.</div>
<div style="text-align: center; font-size: 14px; padding-bottom: 8px;">By using Loritta and/or this website, you agree to our <a href="https://loritta.website/us/privacy" style="color: hsla(0,0%,100%,.6);">Terms of Service &amp; Privacy Policy</a></div>
					""".trimIndent()
					)
				}
			}
			script(type = ScriptType.textJavaScript) {
				unsafe {
					raw("""$('#dummyNavbar').css("height", $('#myTopnav').height())""")
				}
			}
			script(type = ScriptType.textJavaScript) {
				unsafe {
					raw("""
						var emojis = (function() {
  // Set the size of the rendered Emojis
  // This can be set to 16x16, 36x36, or 72x72
  twemoji.size = '72x72';

  // Parse the document body and
  // insert <img> tags in place of Unicode Emojis
  twemoji.parse(document.body);
}(window, twemoji));

// Wait for document to finish loading
function ready(cb) {
  if (document.readyState != 'loading') {
    cb();
  } else {
    document.addEventListener('DOMContentLoaded', cb);
  }
}

ready(emojis);

/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function myFunction() {
	var element = ${'$'}("#myTopnav");
	if (!element.hasClass("responsive")) {
		element.addClass("responsive")
	} else {
	element.removeClass("responsive")
	}
}
					""".trimIndent())
				}
			}
		}
	}
}