package net.perfectdreams.gabriela.commands

import net.perfectdreams.gabriela.commands.vanilla.BotCommand
import net.perfectdreams.gabriela.commands.vanilla.PingCommand
import net.perfectdreams.gabriela.commands.vanilla.SupportCommand

class CommandManager {
	val commands = mutableListOf<AbstractCommand>()

	init {
		commands.add(PingCommand())
		commands.add(BotCommand())
		commands.add(SupportCommand())
	}
}