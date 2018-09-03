package net.perfectdreams.gabriela

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.perfectdreams.gabriela.commands.CommandManager
import net.perfectdreams.gabriela.listeners.MessageListener
import net.perfectdreams.gabriela.listeners.UserListener
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.GabrielaConfig
import net.perfectdreams.gabriela.website.BotListWebsite
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class Gabriela(val config: GabrielaConfig) {
	lateinit var jda: JDA
	lateinit var mongoClient: MongoClient
	lateinit var database: MongoDatabase
	lateinit var collection: MongoCollection<DiscordBot>
	val executor = Executors.newCachedThreadPool() // Threads
	val threadPool = Executors.newScheduledThreadPool(40)
	val commandManager = CommandManager()

	fun start() {
		val pojoCodecRegistry = fromRegistries(com.mongodb.MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()))

		val settings = MongoClientSettings.builder()
				.codecRegistry(pojoCodecRegistry)
				.applyConnectionString(ConnectionString("mongodb://${config.mongoDbIp}"))
				.build()

		mongoClient = MongoClients.create(settings)
		database = mongoClient.getDatabase("vespertine_bot_list")
		collection = database.getCollection("bots", DiscordBot::class.java)

		jda = JDABuilder(AccountType.BOT).setToken(config.token).build()
		jda.addEventListener(
				MessageListener(),
				UserListener()
		)

		jda.presence.game = Game.watching("https://bots.perfectdreams.net/")

		thread {
			BotListWebsite(config.websiteUrl, config.frontendFolder).start()
		}
	}
}