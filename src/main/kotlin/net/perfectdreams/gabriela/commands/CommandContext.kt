package net.perfectdreams.gabriela.commands

import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.perfectdreams.gabriela.utils.GabrielaReply

class CommandContext(val event: MessageReceivedEvent, val minecraftUsername: String? = null) {
	val member = event.member
	val user = event.author
	val args: List<String>

	init {
		val _args = event.message.contentRaw.split(" ").toMutableList()
		_args.removeAt(0)
		args = _args
	}

	fun reply(vararg pantufaReplies: GabrielaReply) {
		val message = StringBuilder()
		for (pantufaReply in pantufaReplies) {
			message.append(pantufaReply.build(this) + "\n")
		}
		return sendMessage(message.toString())
	}

	fun sendMessage(content: String) {
		return event.channel.sendMessage(content).queue()
	}

	fun sendMessage(vararg replies: GabrielaReply) {
		val content = replies.joinToString("\n", transform = { it.build(this) })
		return event.channel.sendMessage(content).queue()
	}

	fun sendMessage(content: MessageEmbed) {
		return event.channel.sendMessage(content).queue()
	}
}