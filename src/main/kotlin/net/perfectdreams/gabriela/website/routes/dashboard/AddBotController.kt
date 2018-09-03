package net.perfectdreams.gabriela.website.routes.dashboard

import com.mongodb.client.model.Filters
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.BotLibrary
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.views.GenericError
import net.perfectdreams.gabriela.views.NewBot
import net.perfectdreams.gabriela.views.NewBotSuccess
import org.jooby.Request
import org.jooby.Response
import org.jooby.mvc.GET
import org.jooby.mvc.POST
import org.jooby.mvc.Path

@Path("/dashboard/addbot")
class AddBotController {
	@GET
	fun viewAddBotPage(req: Request, res: Response) {
		res.send(NewBot.build(req))
	}

	@POST
	fun addBot(req: Request, res: Response) {
		val clientId = req.param("client-botId").value()
		val checkbox = req.param("terms-of-use").value("off")

		if (checkbox != "on") {
			res.send(GenericError.build(req, "Você precisa aceitar os termos de uso do VespertineDreams para enviar o seu bot!"))
			return
		}
		val prefix = req.param("prefix").value()
		val reason = req.param("reason").value()
		val programmingLanguage = req.param("programming-language").value()
		val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
		val userIdentification = if (optional.isPresent) optional.get() else return

		val user = GabrielaLauncher.gabriela.jda.retrieveUserById(clientId).complete()

		if (!user.isBot) {
			// Você não pode adicionar uma coisa que não é um bot
			res.send(GenericError.build(req, "Você não pode adicionar algo que não é um bot!"))
			return
		}

		if (GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", user.id)).count() != 0) {
			res.send(GenericError.build(req, "Você não pode adicionar um bot que já está na lista!"))
			return
		}

		val discordBot = DiscordBot(clientId, userIdentification.id).apply {
			this.prefix = prefix
			// this.description = reason
			// Iremos tentar "adivinhar" qual library é
			this.library = when (programmingLanguage.toLowerCase()) {
				"java" -> BotLibrary.JDA
				"javascript" -> BotLibrary.DISCORD_JS
				"rust" -> BotLibrary.DISCORD_RS
				"c#" -> BotLibrary.DISCORD_NET
				"lua" -> BotLibrary.DISCORDIA
				"python" -> BotLibrary.DISCORD_PY
				"go" -> BotLibrary.DISCORD_GO
				"ruby" -> BotLibrary.DISCORDRB
				"swift" -> BotLibrary.SWORD
				else -> BotLibrary.UNKNOWN
			}
			this.generateNewToken()
		}
		GabrielaLauncher.gabriela.collection.insertOne(discordBot)

		Constants.botAnalysisBroadcastChannel.sendMessage(
				"""@everyone
~~--------------------------------------------------------------------------------------------------------------------------------------------~~
O usuário `${userIdentification.username}#${userIdentification.discriminator}` fez uma aplicação para adicionar `${user.name}#${user.discriminator}`!
ID do bot: `$clientId`
Prefixo do bot: `$prefix`
Linguagem de programação: `$programmingLanguage`
Motivo para adicionar o bot:
```
$reason
```
Invite (gerado automaticamente): <https://discordapp.com/api/oauth2/authorize?client_id=$clientId&permissions=0&scope=bot>
Para aprovar, use `g!bot aprovar $clientId`
Para rejeitar, use `g!bot rejeitar $clientId MotivoDaRejeição`
~~--------------------------------------------------------------------------------------------------------------------------------------------~~
			""".trimIndent()
		).queue()
		res.send(NewBotSuccess.build(req))
	}
}