package net.perfectdreams.gabriela.listeners

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela

class MessageListener : ListenerAdapter() {
	override fun onMessageReceived(event: MessageReceivedEvent) {
		gabriela.executor.execute {
			for (command in gabriela.commandManager.commands) {
				if (command.matches(event)) {
					return@execute
				}
			}
		}
	}
}