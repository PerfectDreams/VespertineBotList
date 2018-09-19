package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.User
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.BetterWhitelist
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.DiscordUtils
import net.perfectdreams.gabriela.utils.generateAd
import org.jooby.Request
import org.jooby.Response
import org.jsoup.Jsoup

class ViewBotView(val bot: Member, val botInfo: DiscordBot) : BaseView() {
	override fun getPageTitle(): String {
		return bot.user.name
	}

	override fun getDescription(): String {
		return botInfo.tagline ?: Constants.DEFAULT_DESCRIPTION
	}

	override fun getIcon(): String {
		return bot.user.effectiveAvatarUrl
	}

	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
		val userIdentification = if (optional.isPresent) optional.get() else null

		div {
			id = "content"
			div {
				id = "bot-info"
				div("flex-container") {
					img(classes = "rounded-image", src = bot.user.effectiveAvatarUrl + "?size=256") {
						width = "200"
						height = "200"
					}
					div(classes = "misc-info") {
						div(classes = "title") {
							+bot.user.name
						}
						div {
							span(classes = "button") {
								span(classes = "left-button") {
									i(classes = "fas fa-plus")
								}
								span(classes = "right-button") {
									a(href = "$WEBSITE_URL/bot/${botInfo.botId}/invite") {
										+" Adicionar"
									}
								}
							}
							span(classes = "button") {
								a(href = "$WEBSITE_URL/bot/${botInfo.botId}/vote") {
									i(classes = "far fa-thumbs-up")
									+" Votar"
								}
							}
							if (!botInfo.websiteUrl.isNullOrBlank()) {
								span(classes = "button") {
									a(href = "$WEBSITE_URL/bot/${botInfo.botId}/website", target = "_blank") {
										i(classes = "fas fa-mouse-pointer")
										+" Website"
									}
								}
							}
							if (!botInfo.supportUrl.isNullOrBlank()) {
								span(classes = "button") {
									a(href = "$WEBSITE_URL/bot/${botInfo.botId}/support", target = "_blank") {
										i(classes = "fas fa-question-circle")
										+" Servidor de Suporte"
									}
								}
							}
							if (!botInfo.repositoryUrl.isNullOrBlank()) {
								span(classes = "button") {
									a(href = "$WEBSITE_URL/bot/${botInfo.botId}/repository", target = "_blank") {
										i(classes = "fab fa-github-alt")
										+" GitHub"
									}
								}
							}
							if (userIdentification != null) {
								if (botInfo.ownerId == userIdentification.id || botInfo.ownerIds.contains(userIdentification.id)) {
									span(classes = "button") {
										a(href = "$WEBSITE_URL/bot/${botInfo.botId}/configure") {
											i(classes = "fas fa-wrench")
											+" Configurar"
										}
									}
								}
							}
						}
						div(classes = "tagline") {
							+(botInfo.tagline ?: Constants.DEFAULT_DESCRIPTION)
						}
						if (botInfo.categories.isNotEmpty()) {
							div(classes = "tags") {
								i(classes = "fas fa-tags")
								+ " "
								for (category in botInfo.categories) {
									span {
										+ category.title
									}
									if (botInfo.categories.last() != category) {
										+", "
									}
								}
							}
						}
						if (botInfo.getServerCount() != 0) {
							div(classes = "guild-status") {
								+"${botInfo.getServerCount()} servidores"

							}
						}
					}
				}
				div(classes = "center-text") {
					val owners = mutableSetOf<User>()
					owners.add(gabriela.jda.getUserById(botInfo.ownerId))
					owners.addAll(botInfo.ownerIds.mapNotNull { gabriela.jda.getUserById(it) })
					+"Criado por "
					for (owner in owners) {
						span {
							val avatarUrl = DiscordUtils.getUserAvatarUrl(owner.id)
							img(src = avatarUrl, classes = "rounded-image owner-avatar") {
								width = "24"
								height = "24"
							}
							+ " "
							span(classes = "owner-name") {
								+owner.name
							}
							span(classes = "owner-discriminator") {
								+("#" + owner.discriminator)
							}
						}
						if (owners.last() != owner) {
							+" "
						}
					}
				}
				hr {}
			}
			generateAd()
			generateAd()
			hr {}
			div {
				val parsed = Constants.parser.parse(botInfo.description ?: Constants.DEFAULT_DESCRIPTION)
				val rendered = Constants.renderer.render(parsed)
				// Agora nós iremos parsear pelo jsoup para remover qualquer tag feia
				val wl = BetterWhitelist()
				val cleanText = Jsoup.clean(rendered, wl)
				unsafe {
					raw(cleanText)
				}
			}
		}
	}
}