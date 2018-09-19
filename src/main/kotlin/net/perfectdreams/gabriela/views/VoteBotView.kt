package net.perfectdreams.gabriela.views

import com.github.salomonbrys.kotson.jsonObject
import kotlinx.html.*
import net.dv8tion.jda.core.entities.Member
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.*
import org.jooby.Request
import org.jooby.Response
import java.util.*

class VoteBotView(val userIdentification: TemmieDiscordAuth.UserIdentification?, val bot: Member, val botInfo: DiscordBot) : BaseView() {
	override fun getPageTitle(): String {
		return bot.user.name
	}

	override fun getDescription(): String {
		return botInfo.tagline ?: Constants.DEFAULT_DESCRIPTION
	}

	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		div {
			id = "content"
			div {
				id = "container"
				div {
					id = "text-container"
					classes += "center-text"
					generateHeader(
							ImageIcon(bot.user.effectiveAvatarUrl),
							"Votar em ${bot.user.name}",
							"Quanto mais votos, mais as chances de outras pessoas conseguirem encontrar (e usarem) o bot!"
					)

					val validVotes = botInfo.votes.filter { it.votedAt > System.currentTimeMillis() - 2592000000 }
					val map = mutableMapOf<String, Int>()
					for (vote in validVotes) {
						map[vote.id] = map.getOrDefault(vote.id, 0) + 1
					}

					div("pure-g") {
						div("pure-u-1 pure-u-md-1-2") {
							div {
								style = "display: flex;flex-direction: column;justify-content: center;width: 100%;height: 100%;"
								if (userIdentification != null) {
									val canVote = botInfo.canUpvote(userIdentification.id, req.trueIp)

									if (canVote) {
										form(method = FormMethod.post) {
											button(classes = "g-recaptcha button", type = ButtonType.submit) {
												this.attributes["data-sitekey"] = "6Le3DG0UAAAAAP2hh9IQA4IwJhmQHaNEN3mQzz6M"
												this.attributes["data-callback"] = "onSubmit"
												i(classes = "far fa-thumbs-up")
												+" Votar"
											}
										}
									} else {
										div {
											+ "Você já votou hoje!"
										}
									}
								} else {
									val state = jsonObject(
											"redirectUrl" to Constants.WEBSITE_URL + "/bots/" + botInfo.botId + "/vote"
									)

									span {
										a(classes = "button", href = (Constants.AUTHORIZATION_URL + "&state=${Base64.getEncoder().encodeToString(state.toString().toByteArray()).encodeToUrl()}")) {
											i(classes = "far fa-thumbs-up")
											+" Votar"
										}
									}
								}
								generateAd()
							}
						}
						div("pure-u-1 pure-u-md-1-2") {
							table {
								style = "font-weight: 600;"
								tr {
									th {
										+ "Posição"
									}
									th {
										+ ""
									}
									th {
										+ "Nome"
									}
								}
								for ((index, vote) in map.entries.sortedByDescending { it.value }.withIndex()) {
									val user = GabrielaLauncher.gabriela.jda.getUserById(vote.key) ?: GabrielaLauncher.gabriela.jda.retrieveUserById(vote.key).complete()

									if (index == 10)
										break
									tr {
										td {
											+"#${index + 1}"
										}
										td {
											style = "text-align: center;"
											img(src = user.effectiveAvatarUrl, classes = "rounded-image") {
												if (index == 0) {
													style = "width: 48px;"
													style = "height: 48px;"
												}
												if (index == 1) {
													style = "width: 42px;"
													style = "height: 42px;"
												}
												if (index == 2) {
													style = "width: 36px;"
													style = "height: 36px;"
												}
												if (index == 3) {
													style = "width: 30px;"
													style = "height: 30px;"
												}
												if (index == 4) {
													style = "width: 24px;"
													style = "height: 24px;"
												}
												if (index >= 5) {
													style = "width: 18px;"
													style = "height: 18px;"
												}
											}
										}
										td {
											if (index == 0) {
												style = "font-size: 1.5em"
												classes = setOf("rainbow")
											}
											if (index == 1) {
												style = "font-size: 1.4em"
											}
											if (index == 2) {
												style = "font-size: 1.3em"
											}
											if (index == 3) {
												style = "font-size: 1.2em"
											}
											if (index == 4) {
												style = "font-size: 1.1em"
											}
											+ "${user.name}#${user.discriminator}"
										}
									}
								}
							}
						}
					}
					generateAd()
				}
			}
		}
		script(type = ScriptType.textJavaScript) {
			unsafe {
				raw(
						"""function onSubmit(token) { VespertineBotListJS.VoteBotStuff.onSubmit(token); }"""
				)
			}
		}
	}
}