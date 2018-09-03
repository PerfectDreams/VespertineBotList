package net.perfectdreams.gabriela.models

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

class BotShard @BsonCreator constructor(
		@BsonProperty("id")
		val id: Int,
		@BsonProperty("guildCount")
		var guildCount: Int
)