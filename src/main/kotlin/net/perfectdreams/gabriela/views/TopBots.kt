package net.perfectdreams.gabriela.views

import kotlinx.html.div
import kotlinx.html.id
import net.dv8tion.jda.core.entities.Member
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.Constants.botListGuild
import net.perfectdreams.gabriela.utils.generateBotInfo
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

object TopBots {
	fun build(req: Request, res: Response, title: String, botInfos: List<DiscordBot>) = Base.build(req, title, null) {
		div {
			id = "content"
			generateHeader(
					"fas fa-search",
					title,
					"Pesquisamos, procuramos e reviramos todos os bots da nossa lista, e foi isto que nós encontramos!"
			)

			val results = mutableListOf<Pair<Member, DiscordBot>>()

			// Procurar pela tagline do bot
			for (botInfo in botInfos) {
				val member = botListGuild.getMemberById(botInfo.botId) ?: continue
				results.add(Pair(member, botInfo))
			}

			div("pure-g") {
				for ((member, botInfo) in results) {
					generateBotInfo(botInfo)
				}
			}
		}
	}
}