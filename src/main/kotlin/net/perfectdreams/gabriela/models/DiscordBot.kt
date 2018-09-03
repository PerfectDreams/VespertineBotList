package net.perfectdreams.gabriela.models

import com.google.gson.JsonObject
import net.perfectdreams.gabriela.utils.*
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.internal.Base64
import java.util.*

class DiscordBot @BsonCreator constructor(
		@BsonProperty("_id")
		@get:[BsonProperty("_id")]
		val botId: String,
		@BsonProperty("ownerId")
		val ownerId: String
) {
	var prefix: String? = null
	var approved = false
	var tagline: String? = null
	var description: String? = null
	var guildCount = 0
	var shardCount = mutableListOf<BotShard>()
	var ownerIds = mutableListOf<String>()
	var library = BotLibrary.UNKNOWN
	var color = EntryColor.BLURPLE
	var categories = mutableListOf<BotCategory>()
	var subCategories = mutableListOf<BotSubcategory>()
	var specialRanks = mutableListOf<SpecialRank>()
	var reviews = mutableListOf<Review>()
	var votes = mutableListOf<Vote>()
	var supportedLanguages = mutableListOf<Language>()
	var inviteUrl: String? = null
	var websiteUrl: String? = null
	var repositoryUrl: String? = null
	var supportUrl: String? = null
	var token: String? = null
	var lastBump: Long = 0
	var webhookUrl: String? = null

	fun getServerCount(): Int {
		return if (shardCount.isEmpty()) {
			guildCount
		} else {
			shardCount.sumBy { it.guildCount }
		}
	}

	fun generateNewToken() {
		val bytes = ByteArray(512)
		Constants.slowRandom.nextBytes(bytes)
		token = Base64.encode(bytes)
	}

	fun canUpvote(id: String, ip: String): Boolean {
		// Para evitar pessoas criando várias contas e votando, nós iremos também verificar o IP dos usuários que votarem
		// Isto evita pessoas farmando upvotes votando (claro que não é um método infalível, mas é melhor que nada, né?)
		val vote = votes.lastOrNull {
			it.id == id || it.ip == ip
		}

		if (vote != null) {
			val votedAt = vote.votedAt

			val calendar = Calendar.getInstance()
			calendar.timeInMillis = votedAt
			calendar.set(Calendar.HOUR_OF_DAY, 0)
			calendar.set(Calendar.MINUTE, 0)
			calendar.add(Calendar.DAY_OF_MONTH, 1)
			val tomorrow = calendar.timeInMillis

			val canVote = System.currentTimeMillis() > tomorrow

			return canVote
		}
		return true
	}
}