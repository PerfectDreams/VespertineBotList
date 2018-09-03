package net.perfectdreams.gabriela.listeners

import com.mongodb.client.model.Filters
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.Collections
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.Constants.botListGuild

class UserListener : ListenerAdapter() {
	override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
		val member = event.member

		if (!event.user.isBot)
			return

		if (event.guild.id != Constants.GUILD_ID)
			return

		val role = event.guild.getRoleById("481255938570059787")

		event.guild.controller.addSingleRoleToMember(member, role).queue()
	}

	override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
		// Automaticamente retirar bots de usuários que sairam do servidor
		val member = event.member

		if (event.guild.id != Constants.GUILD_ID)
			return

		gabriela.executor.execute {
			if (member.user.isBot) {
				// Se é um bot que saiu...
				Collections.users.deleteMany(Filters.eq("_id", member.user.id))
			} else {
				val bots = Collections.users.find(Filters.eq("ownerId", member.user.id))

				bots.iterator().use {
					while (it.hasNext()) {
						val bot = it.next()

						if (bot is DiscordBot) {
							val id = bot.botId

							val memberBot = botListGuild.getMemberById(id) ?: continue
							botListGuild.controller.kick(memberBot).queue()
						}
					}
				}

				// Remover a database todos os bots que o dono fez
				Collections.users.deleteMany(Filters.eq("ownerId", member.user.id))
			}
		}
	}
}