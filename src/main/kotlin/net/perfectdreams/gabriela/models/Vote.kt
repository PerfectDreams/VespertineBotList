package net.perfectdreams.gabriela.models

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

class Vote @BsonCreator constructor(
		@BsonProperty("id")
		val id: String,
		@BsonProperty("votedAt")
		val votedAt: Long,
		@BsonProperty("ip")
		val ip: String,
		@BsonProperty("email")
		val email: String
)