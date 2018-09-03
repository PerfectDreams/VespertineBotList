package net.perfectdreams.gabriela.commands.vanilla

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.commands.AbstractCommand
import net.perfectdreams.gabriela.commands.CommandContext
import net.perfectdreams.gabriela.commands.annotation.ArgumentType
import net.perfectdreams.gabriela.commands.annotation.InjectArgument
import net.perfectdreams.gabriela.commands.annotation.Subcommand
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.WEBSITE_URL
import net.perfectdreams.gabriela.utils.Constants.botAnalysisGuild
import net.perfectdreams.gabriela.utils.Constants.botListGuild
import net.perfectdreams.gabriela.utils.Constants.verifyRole
import net.perfectdreams.gabriela.utils.GabrielaReply

class BotCommand : AbstractCommand("bot") {
	@Subcommand
	fun onPing(context: CommandContext) {
		context.reply(
				GabrielaReply(
						"owo whats this?"
				)
		)
	}

	@Subcommand(["rejeitar"])
	fun onReject(context: CommandContext, botId: String, @InjectArgument(ArgumentType.ARGUMENT_LIST) reason: String) {
		if (!botListGuild.getMember(context.user).roles.contains(verifyRole))
			return

		val user = gabriela.jda.getUserById(botId)

		if (user == null) {
			context.reply(
					GabrielaReply(
							"Você tem certeza que este bot está aqui? Ou você está tentando aprovar alguém que você nem testou? Que feio..."
					)
			)
			return
		}

		val botInfo = gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()

		if (botInfo == null) {
			context.reply(
					GabrielaReply(
							"Mas... esse usuário nem está no meu banco de dados de bots! Tem certeza que você escreveu certinho?"
					)
			)
			return
		}

		val owner = Constants.botListGuild.jda.getUserById(botInfo.ownerId)

		gabriela.collection.deleteOne(Filters.eq("_id", botId))

		context.reply(
				GabrielaReply(
						"${user.asMention} foi rejeitado! <a:happy:483299817234235402>"
				)
		)

		if (owner != null) {
			owner.openPrivateChannel().queue {
				it.sendMessage("""Nós analisamos o seu bot, testamos ele, verificamos ele, mexemos nele... e concluimos que...
				|
				|**O seu bot foi rejeitado... <:sad_cat:483313979691892756>**
				|Motivo: `$reason`
				|
				|Que pena... tente melhorar o seu bot para corrigir os problemas que encontramos para que ele possa ser adicionado na nossa lista!
			""".trimMargin()).queue()
			}
		}

		botAnalysisGuild.controller.kick(user.id, "Bot rejeitado por ${context.user.name}#${context.user.discriminator} (${context.user.id})").queue()
	}

	@Subcommand(["aprovar"])
	fun onApprove(context: CommandContext, botId: String) {
		if (!botListGuild.getMember(context.user).roles.contains(verifyRole))
			return

		val user = gabriela.jda.getUserById(botId)

		if (user == null) {
			context.reply(
					GabrielaReply(
							"Você tem certeza que este bot está aqui? Ou você está tentando aprovar alguém que você nem testou? Que feio..."
					)
			)
			return
		}

		val botInfo = gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()

		if (botInfo == null) {
			context.reply(
					GabrielaReply(
							"Mas... esse usuário nem está no meu banco de dados de bots! Tem certeza que você escreveu certinho?"
					)
			)
			return
		}

		val owner = Constants.botListGuild.jda.getUserById(botInfo.ownerId)

		if (owner == null) {
			context.reply(
					GabrielaReply(
							"O criador deste bot não está no VespertineDreams! Gente idiota, parece que não sabem ler que precisa ficar no servidor caso queira ter um bot na lista. \uD83D\uDE44"
					)
			)
			return
		}

		gabriela.collection.updateOne(Filters.eq("_id", botId), Updates.set("approved", true))

		context.reply(
				GabrielaReply(
						"${user.asMention} foi aprovado! <a:happy:483299817234235402>"
				)
		)

		owner.openPrivateChannel().queue {
			it.sendMessage("""Nós analisamos o seu bot, testamos ele, verificamos ele, mexemos nele... e concluimos que...
				|
				|**O seu bot foi aprovado! <a:happy:483299817234235402>**
				|Parabéns! O seu bot foi aprovado e agora está público na nossa lista de bots!
				|
				|$WEBSITE_URL/bot/$botId
			""".trimMargin()).queue()
		}

		botAnalysisGuild.controller.kick(user.id, "Bot aprovado por ${context.user.name}#${context.user.discriminator} (${context.user.id})").queue()
	}
}