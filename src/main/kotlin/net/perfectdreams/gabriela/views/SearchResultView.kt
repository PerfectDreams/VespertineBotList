package net.perfectdreams.gabriela.views

import com.mongodb.client.model.Filters
import com.mongodb.client.model.TextSearchOptions
import kotlinx.html.DIV
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.style
import net.dv8tion.jda.core.entities.Member
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.BotCategory
import net.perfectdreams.gabriela.utils.Constants.botListGuild
import net.perfectdreams.gabriela.utils.generateBotInfo
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

class SearchResultView : BaseView() {
	override fun getPageTitle(): String {
		return "Resultados da Pesquisa"
	}

	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		div {
			id = "content"
			generateHeader(
					"fas fa-search",
					"Resultados de sua pesquisa",
					"Pesquisamos, procuramos e reviramos todos os bots da nossa lista, e foi isto que nós encontramos!"
			)
			val query = req.param("q").value()
			println("Searching for $query")
			val lowerCaseQuery = query.toLowerCase()
			val searchCategories = mutableListOf<BotCategory>()
			val searchSubCategories = mutableListOf<BotCategory>()

			val results = mutableListOf<Pair<Member, DiscordBot>>()

			// Procurar pelo nome do bot
			val searchByNameResults = botListGuild.members.filter {
				it.user.isBot && it.user.name.contains(lowerCaseQuery, true)
			}

			val botInfos = gabriela.collection.find(Filters.`in`("_id", searchByNameResults.map { it.user.id })).toMutableList()

			println("Teve ${botInfos.size} resultados!")
			for (member in searchByNameResults) {
				val botInfo = botInfos.firstOrNull { it.botId == member.user.id } ?: continue
				if (!botInfo.approved)
					continue
				results.add(Pair(member, botInfo))
			}

			// Procurar pela tagline do bot
			val searchByTaglineResults = gabriela.collection.find(Filters.text(lowerCaseQuery, TextSearchOptions().caseSensitive(true))).toMutableList()
			for (botInfo in searchByTaglineResults) {
				val member = botListGuild.getMemberById(botInfo.botId) ?: continue
				if (!botInfo.approved)
					continue
				results.add(Pair(member, botInfo))
			}

			if (results.isNotEmpty()) {
				div("pure-g") {
					for ((member, botInfo) in results) {
						generateBotInfo(botInfo)
					}
				}
			} else {
				div {
					style = "text-align: center;"
					+ "¯\\_(ツ)_/¯"
				}
			}
		}
	}
}