package net.perfectdreams.gabriela.commands.vanilla

import net.perfectdreams.gabriela.commands.AbstractCommand
import net.perfectdreams.gabriela.commands.CommandContext
import net.perfectdreams.gabriela.commands.annotation.Subcommand
import net.perfectdreams.gabriela.utils.GabrielaReply

class PingCommand : AbstractCommand("ping") {
	@Subcommand
	fun onPing(context: CommandContext) {
		context.reply(
				GabrielaReply(
						"Pong!"
				)
		)
	}

	@Subcommand(["wow"])
	fun onWow(context: CommandContext) {
		context.reply(
				GabrielaReply(
						"wow, such bot, very fun"
				)
		)
	}
}