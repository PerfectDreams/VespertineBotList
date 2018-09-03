package net.perfectdreams.gabriela.views

import com.mongodb.client.model.Filters
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.generateBotInfo
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

object Dashboard {
	fun build(req: Request, res: Response) = Base.build(req, "Painel", null) {
		div {
			id = "content"
			val userIdentification = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification").get()

			val bots = gabriela.collection.find(
					Filters.and(
							Filters.or(
									Filters.eq("ownerId", userIdentification.id),
									Filters.`in`("ownerIds", userIdentification.id)
							),
							Filters.eq("approved", true)
					)
			)

			generateHeader(
					"fas fa-robot",
					"Seus Bots",
					"Lista com os bots que você enviou no Vespertine's Bot List!"
			)

			div("pure-g") {
				for (bot in bots) {
					generateBotInfo(bot) {
						div("pure-g") {
							div("pure-u-1 pure-u-md-1-1") {
								div(classes = "more-info-button") {
									a(href="${Constants.WEBSITE_URL}/bot/${bot.botId}/configure") {
										+ "Configurar"
									}
								}
							}
						}
					}
				}
			}

			div(classes = "view-more-button-wrapper") {
				div(classes = "button view-more-button") {
					a(href = "$WEBSITE_URL/dashboard/addbot") {
						i(classes = "fas fa-plus")
						+" Adicionar Bot"
					}
				}
			}

			generateHeader(
					"fas fa-list",
					"Bots em análise",
					"Lista com os bots que você enviou, mas ainda não foram analisados pela a equipe!"
			)

			val notApprovedYet = gabriela.collection.find(
					Filters.and(
							Filters.eq("ownerId", userIdentification.id),
							Filters.eq("approved", false)
					)
			).toMutableList()

			for (botInfo in notApprovedYet) {
				div {
					+ botInfo.botId
				}
			}
		}
	}
}