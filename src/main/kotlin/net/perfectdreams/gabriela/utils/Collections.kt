package net.perfectdreams.gabriela.utils

import com.mongodb.client.MongoCollection
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.models.DiscordBot

object Collections {
	val users: MongoCollection<DiscordBot> by lazy {
		gabriela.database.getCollection("bots", DiscordBot::class.java)
	}
}