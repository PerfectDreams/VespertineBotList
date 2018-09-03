package net.perfectdreams.gabriela.commands.vanilla

import net.perfectdreams.gabriela.commands.AbstractCommand
import net.perfectdreams.gabriela.commands.CommandContext
import net.perfectdreams.gabriela.commands.annotation.Subcommand
import net.perfectdreams.gabriela.utils.GabrielaReply

class SupportCommand : AbstractCommand("suporte") {
	val roles = mapOf(
			"bukkit" to "485202955759386626",
			"discord.py" to "485202939254931477",
			"discord.js" to "485202934502522890",
			"python" to "485202953897246721",
			"kotlin" to "485202952223588354",
			"java" to "485202949442895893",
			"jda" to "485202955939610655",
			"javascript" to "485246187235049484"
	)

	@Subcommand
	fun root(context: CommandContext) {
		context.reply(
				GabrielaReply(
						"Você gosta de ajudar as outras pessoas? Então pegue um cargo de suporte sobre a tecnologia que você deseja ajudar!"
				),
				GabrielaReply(
						"Cargos disponíveis: ${roles.keys.joinToString(", ", transform = { "`$it`" })}"
				),
				GabrielaReply(
						"Para pegar um cargo, use `g!suporte NomeDoCargo`"
				)
		)
	}

	@Subcommand
	fun onRole(context: CommandContext, roleName: String) {
		val roleId = roles[roleName]

		if (roleId == null) {
			context.reply(
					GabrielaReply(
							"Mas... esse cargo aí não existe!"
					)
			)
			return
		}

		val role = context.event.guild.getRoleById(roleId)

		if (role == null) {
			context.reply(
					GabrielaReply(
							"Mas... esse cargo aí não existe!"
					)
			)
			return
		}

		if (context.member.roles.contains(role)) {
			context.event.guild.controller.removeSingleRoleFromMember(context.member, role).queue()
			context.reply(
					GabrielaReply(
							"Então quer dizer que o trampo de `${role.name}` foi muito difícil para você? Que pena..."
					)
			)
		} else {
			context.event.guild.controller.addSingleRoleToMember(context.member, role).queue()
			context.reply(
					GabrielaReply(
							"Você recebeu o cargo de `${role.name}`! Agora ajude os membros que tem dúvidas e se divirta! <a:happy:483299817234235402>"
					)
			)
		}
	}
}