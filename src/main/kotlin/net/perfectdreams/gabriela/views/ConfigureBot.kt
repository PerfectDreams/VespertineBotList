package net.perfectdreams.gabriela.views

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.html.*
import net.dv8tion.jda.core.entities.Member
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.*
import org.jooby.Request
import java.util.*
import javax.crypto.spec.SecretKeySpec

object ConfigureBot {
	fun build(req: Request, botInfo: DiscordBot, bot: Member) = Base.build(req, "Configurar Bot", null) {
		div {
			id = "content"
			div {
				id = "container"
				div {
					id = "text-container"
					generateHeader(
							"fas fa-cogs",
							"Configurar ${bot.user.name}",
							"Configure o seu bot para que fique mais bonito e maravilhoso em nossa lista!"
					)

					form(method = FormMethod.post) {
						div(classes = "flavour-text") {
							+ "Bibiloteca do Bot"
						}
						select {
							name = "botLibrary"
							for (library in BotLibrary.values()) {
								option {
									attributes["value"] = library.name
									if (botInfo.library == library)
										attributes["selected"] = "true"

									+ library.title
								}
							}
						}
						hr {}
						div(classes = "flavour-text") {
							+ "Cor do Bot"
						}
						select {
							name = "botColor"
							for (color in EntryColor.values()) {
								option {
									attributes["value"] = color.name
									if (botInfo.library == color)
										attributes["selected"] = "true"

									+ color.name
								}
							}
						}
						hr {}
						div(classes = "flavour-text") { + "Prefixo do Bot" }
						input(name = "prefix", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.prefix ?: ""
						}
						hr {}
						div(classes = "flavour-text") { + "Convite do Bot" }
						input(name = "inviteUrl", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.inviteUrl ?: ""
						}
						hr {}
						div(classes = "flavour-text") { + "Website do Bot" }
						input(name = "websiteUrl", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.websiteUrl ?: ""
						}
						hr {}
						div(classes = "flavour-text") { + "Suporte" }
						input(name = "supportUrl", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.supportUrl ?: ""
						}
						hr {}
						div(classes = "flavour-text") { + "Repositório do Bot" }
						input(name = "repositoryUrl", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.repositoryUrl ?: ""
						}
						hr {}
						div(classes = "flavour-text") { + "Categorias (máximo cinco)" }
						div {
							+ "Escolha as categorias que o seu bot possui, selecionar categorias ajuda outras pessoas a encontrarem o seu bot! Por favor, apenas escolha o que o seu bot realmente tem, caso pessoas reportem que o seu bot está em categorias não relacionadas com o seu bot, seu bot poderá ser removido da lista!"
						}
						div {
							id = "config-categories"
							div("pure-g") {
								for (category in BotCategory.values()) {
									div("pure-u-1 pure-u-md-1-3") {
										div {
											input(type = InputType.checkBox) {
												name = "category"
												value = category.name
												if (botInfo.categories.contains(category))
													checked = true
											}
											+" ${category.title}"
										}
									}
								}
							}
						}
						hr {}
						div(classes = "flavour-text") { + "Subcategorias" }
						div {
							+ "As vezes o seu bot é mais específico, e por isto existem as subcategorias!"
						}
						div {
							id = "config-sub-categories"
							div("pure-g") {
								for (category in BotSubcategory.values()) {
									div("pure-u-1 pure-u-md-1-3") {
										div("display-none subcategory-${category.name.toLowerCase().replace("_", "-")}") {
											input(type = InputType.checkBox) {
												name = "subcategory"
												value = category.name
												if (botInfo.subCategories.contains(category))
													checked = true
											}
											+" ${category.title}"
										}
									}
								}
							}
						}
						hr {}

						div(classes = "flavour-text") { + "Tagline" }
						input(name = "tagline", type = InputType.text) {
							style = "width: 100%"
							value = botInfo.tagline ?: Constants.DEFAULT_DESCRIPTION
						}
						hr {}

						div(classes = "flavour-text") { + "Descrição" }
						textArea {
							name = "description"
							style = "width: 100%"
							+ (botInfo.description ?: Constants.DEFAULT_DESCRIPTION)
						}
						hr {}
						input(name = "type", type = InputType.hidden) {
							value = "update"
						}
						button(type = ButtonType.submit) {
							+ "Salvar"
						}
					}
					div {
						generateHeader(
								"fas fa-terminal",
								"Token",
								"Integre o seu bot com a Vespertine's Bot List!"
						)

						if (botInfo.token != null) {
							//The JWT signature algorithm we will be using to sign the token
							val signatureAlgorithm = SignatureAlgorithm.HS256

							val nowMillis = System.currentTimeMillis()
							val now = Date(nowMillis)

							//We will sign our JWT with our ApiKey secret
							val signingKey = SecretKeySpec(Base64.getDecoder().decode(botInfo.token), signatureAlgorithm.jcaName)

							//Let's set the JWT Claims
							val builder = Jwts.builder()
									.claim("id", botInfo.botId)
									.setIssuedAt(now)
									.claim("type", "bot")
									.setIssuer("VespertineBotList")
									.signWith(signingKey)

							div {
								style = "overflow: auto;"
								code {
									style = "white-space: nowrap;"
									+ builder.compact()
								}
							}
						}

						form(method = FormMethod.post) {
							input(name = "type", type = InputType.hidden) {
								value = "reset"
							}
							button(type = ButtonType.submit) {
								+ "Gerar um novo Token"
							}
						}
					}
					div {
						generateHeader(
								"fas fa-angle-double-up",
								"Promover seu Bot",
								"Você pode promover o seu bot a cada quatro horas!"
						)

						if (botInfo.lastBump + 14_400_000 > System.currentTimeMillis()) {
							div {
								+ "Ainda não se passaram quatro horas desde a sua última promoção!"
							}
						} else {
							form(method = FormMethod.post) {
								input(name = "type", type = InputType.hidden) {
									value = "promote"
								}
								button(type = ButtonType.submit, classes = "button") {
									+ "Promover"
								}
							}
						}
					}
				}
			}
		}
		script(type = ScriptType.textJavaScript) {
			unsafe {
				raw("""VespertineBotListJS.ConfigureBotStuff.start()""")
			}
		}
	}
}