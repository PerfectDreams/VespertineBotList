package net.perfectdreams.gabriela.utils

import net.dv8tion.jda.core.entities.User
import net.perfectdreams.gabriela.commands.CommandContext

class GabrielaReply(
		val message: String = " ",
		val prefix: String? = null,
		val hasPadding: Boolean = true,
		val mentionUser: Boolean = true
) {
	fun build(commandContext: CommandContext): String {
		var send = ""
		if (prefix != null) {
			send = prefix + " **|** "
		} else if (hasPadding) {
			send = Constants.LEFT_PADDING + " **|** "
		}
		if (mentionUser) {
			send = send + commandContext.member.asMention + " "
		}
		send += message
		return send
	}

	fun build(user: User): String {
		var send = ""
		if (prefix != null) {
			send = prefix + " **|** "
		} else if (hasPadding) {
			send = Constants.LEFT_PADDING + " **|** "
		}
		if (mentionUser) {
			send = send + user.asMention + " "
		}
		send += message
		return send
	}
}