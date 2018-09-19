package net.perfectdreams.gabriela.views

import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.html.*
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.utils.*
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.Constants.botListGuild
import net.perfectdreams.gabriela.utils.Constants.gson
import org.jooby.Request
import org.jooby.Response

class HomeView : BaseView() {
	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		div {
			id = "page-header"
			div("wrapper") {
				h1 {
					+"Discord Bots"
				}
				h2 {
					+"Te ajudando a transformar o seu servidor em algo incrível!"
				}
				div(classes = "interactive-wrapper") {
					div {
						div(classes = "cycling-header") {
							+"Eu quero um bot..."
						}
						div(classes = "cycling-text") {
							+""
						}
						div(classes = "hidden-big-text") {
							style = "visibility: hidden;flex-wrap: wrap;height: 0px;"
							+""
						}
						div {
							style = "flex-wrap: wrap;"
							hr {}
							div {
								style = "width: 27em; margin-left: auto; margin-right: auto; text-align: center;"
								div {
									+"Transforme o seu servidor em um lugar mais agradável, divertido e extraordinário com os bots de nossa lista!"
								}
								div {
									style = "margin-top: 16px;"
									+"Com mais de ${gabriela.collection.find().count()} bots diferentes para você escolher, eu tenho certeza que você irá encontrar um que você irá amar!"
									/* button {
										+ "Explorar Tags"
									} */
									a(href = "https://discord.gg/PBAFd3V") {
										button(classes = "color-blurple button") {
											i("fab fa-discord")
											+" Nosso Servidor"
										}
									}
								}
							}
						}
					}
					div(classes = "profile-wrapper") {
						id = "page-profile-wrapper"
						div(classes = "avatar-wrapper") {
							div {
								img(classes = "rounded-image", src = "https://cdn.discordapp.com/emojis/395010059157110785.png?v=1") {
									id = "profile-avatar"
									width = "112"
									height = "112"
								}
							}
							div(classes = "profile-name") {
								id = "profile-name"
								+"Loritta#0219"
							}
						}
						div(classes = "description-wrapper color-blurple") {
							id = "description-wrapper"
							div(classes = "bot-description") {
								style = "flex-grow: 1;"
								+ "Olá, mundo!"
							}
							div {
								style = "text-align: center;"
								hr {}
								div(classes = "button") {
									style = "text-align: center;"
									a(classes = "bot-linky") {
										+"Ver mais"
									}
								}
							}
						}
					}
				}
			}
		}
		div {
			id = "content"
			generateAd()
			generateAd()
			generateHeader("far fa-thumbs-up",
					"Os melhores bots para o Discord",
					"Procurando novos bots para o seu servidor? Então veja a nossa lista mostrando os bots mais recomendados pelos membros de nossa plataforma!"
			)


			val query = org.bson.Document.parse("{ \$addFields: { \"validVotes\": { \$filter: { input: \"\$votes\", as: \"item\", cond: {\$gt: [\"\$\$item.votedAt\", ${System.currentTimeMillis() - 2592000000}]}}}}}")
			val bestBots = gabriela.collection
					.aggregate(
							listOf(
									Aggregates.match(Filters.eq("approved", true)),
									query,
									org.bson.Document("\$addFields", org.bson.Document("length", org.bson.Document("\$size", org.bson.Document("\$ifNull", listOf("\$validVotes", emptyList<Any>()))))),
									Aggregates.sort(Sorts.descending("length")),
									Aggregates.limit(12)
							)
					)
					.toMutableList()

			div("pure-g") {
				generateBotsInfo(bestBots)
			}
			div(classes = "view-more-button-wrapper") {
				div(classes = "button view-more-button") {
					a(href = "$WEBSITE_URL/page/top/1") {
						i(classes = "fas fa-plus")
						+" Ver mais"
					}
				}
			}

			generateAd()
			generateAd()
			generateHeader("fas fa-angle-double-up",
					"Bots recentemente promovidos",
					"Bots que foram recentemente promovidos em nossa lista pelos donos de tais bots! Não é por nada não, mas se o dono teve todo o trabalho de ir promover o bot, então eu acho que você deveria dar pelo ou menos uma chance a ele..."
			)

			div("pure-g") {
				val recentlyAddedBots = gabriela.collection.find(Filters.eq("approved", true))
						.sort(Sorts.descending("lastBump"))
						.limit(12)
						.toMutableList()
				generateBotsInfo(recentlyAddedBots)
			}
			div(classes = "view-more-button-wrapper") {
				div(classes = "button view-more-button") {
					a(href = "$WEBSITE_URL/page/promoted/1") {
						i(classes = "fas fa-plus")
						+" Ver mais"
					}
				}
			}

			generateAd()
			generateAd()
			generateHeader("fas fa-random",
					"Bots aleatórios",
					"Alguns bots aleatórios de nossa lista para você ver e usar!"
			)
			div("pure-g") {
				val randomBots = gabriela.collection.aggregate(
						listOf(
								Aggregates.match(Filters.eq("approved", true)),
								Aggregates.sample(12)
						)
				).toMutableList()
				generateBotsInfo(randomBots)
			}

			val categoryArray = JsonArray()
			for (category in BotCategory.values()) {
				val randomBotInfo = gabriela.collection.aggregate(
						listOf(
								Aggregates.match(
										Filters.and(
												Filters.eq("approved", true),
												Filters.`in`("categories", listOf(category.name))
										)
								),
								Aggregates.sample(1),
								Aggregates.limit(1)
						)
				).firstOrNull() ?: continue
				val randomBot = botListGuild.getMemberById(randomBotInfo.botId) ?: continue
				val obj = JsonObject()
				obj["category"] = category.name
				obj["id"] = randomBot.user.id
				obj["username"] = randomBot.user.name
				obj["discriminator"] = randomBot.user.discriminator
				obj["avatarUrl"] = randomBot.user.avatarUrl
				obj["tagline"] = randomBotInfo.tagline ?: Constants.DEFAULT_DESCRIPTION
				categoryArray.add(obj)
			}

			val subCategoryArray = JsonArray()
			for (category in BotSubcategory.values()) {
				val randomBotInfo = gabriela.collection.aggregate(
						listOf(
								Aggregates.match(
										Filters.and(
												Filters.eq("approved", true),
												Filters.`in`("categories", listOf(category.name))
										)
								),
								Aggregates.sample(1),
								Aggregates.limit(1)
						)
				).firstOrNull() ?: continue
				val randomBot = botListGuild.getMemberById(randomBotInfo.botId) ?: continue
				val obj = JsonObject()
				obj["category"] = category.name
				obj["id"] = randomBot.user.id
				obj["username"] = randomBot.user.name
				obj["discriminator"] = randomBot.user.discriminator
				obj["avatarUrl"] = randomBot.user.avatarUrl
				obj["tagline"] = randomBotInfo.tagline ?: Constants.DEFAULT_DESCRIPTION
				subCategoryArray.add(obj)
			}

			div(classes = "display-none") {
				id = "random-bots-category"
				+gson.toJson(categoryArray)
			}

			div(classes = "display-none") {
				id = "random-bots-subcategory"
				+gson.toJson(subCategoryArray)
			}

			script(type = ScriptType.textJavaScript) {
				unsafe {
					raw("""VespertineBotListJS.HomeStuff.start()""".trimIndent()
					)
				}
			}
		}
	}
}