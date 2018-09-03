package net.perfectdreams.gabriela.website.routes.api.bot

import com.github.salomonbrys.kotson.nullInt
import com.github.salomonbrys.kotson.obj
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.models.BotShard
import net.perfectdreams.gabriela.utils.Constants
import net.perfectdreams.gabriela.utils.GabiWebCode
import net.perfectdreams.gabriela.utils.WebsiteUtils
import org.bson.internal.Base64
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.POST
import org.jooby.mvc.PUT
import org.jooby.mvc.Path
import javax.crypto.spec.SecretKeySpec

@Path("/api/v1/bot/:botId/stats")
class BotStatsController {
	@PUT
	@POST
	fun updateStats(req: Request, res: Response) {
		val body = req.body()
		if (!body.isSet) {
			res.status(Status.BAD_REQUEST)
			res.send(WebsiteUtils.createErrorPayload(
					GabiWebCode.MISSING_BODY,
					"The request is missing a valid body!"
			))
			return
		}

		val authorizationHeader = req.header("Authorization")

		if (!authorizationHeader.isSet) {
			res.status(Status.UNAUTHORIZED)
			res.send(
					WebsiteUtils.createErrorPayload(
							GabiWebCode.UNAUTHORIZED,
							"Missing \"Authorization\" header"
					)
			)
			return
		}

		val authorization = authorizationHeader.value()
		val botId = req.param("botId").value()
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()

		if (botInfo == null) {
			res.status(Status.NOT_FOUND)
			res.send(
					WebsiteUtils.createErrorPayload(
							GabiWebCode.UNKNOWN_USER,
							"User $botId doesn't exist!"
					)
			)
			return
		}

		val token = botInfo.token
		val jws = try {
			Jwts.parser()
					.setSigningKey(SecretKeySpec(Base64.decode(token), SignatureAlgorithm.HS256.jcaName))
					.parseClaimsJws(authorization)
		} catch (e: Exception) {
			res.status(Status.FORBIDDEN)
			res.send(
					WebsiteUtils.createErrorPayload(
							GabiWebCode.FORBIDDEN,
							e.message
					)
			)
			return
		}

		val jsonParser = Constants.jsonParser.parse(body.value()).obj
		val guildCount = jsonParser["guildCount"].nullInt
		val shardId = jsonParser["shardId"].nullInt
		val shardCount = jsonParser["shardCount"].nullInt

		if (guildCount != null) {
			if (1 > guildCount) {
				res.status(Status.FORBIDDEN)
				res.send(
						WebsiteUtils.createErrorPayload(
								GabiWebCode.FORBIDDEN,
								"Less or equal to zero guild count!"
						)
				)
				return
			}
			if (shardId != null && shardCount != null) {
				if (0 > shardId) {
					res.status(Status.FORBIDDEN)
					res.send(
							WebsiteUtils.createErrorPayload(
									GabiWebCode.FORBIDDEN,
									"Less than zero shard ID!"
							)
					)
					return
				}
				if (0 > shardCount) {
					res.status(Status.FORBIDDEN)
					res.send(
							WebsiteUtils.createErrorPayload(
									GabiWebCode.FORBIDDEN,
									"Less than zero shard count!"
							)
					)
					return
				}
				// Usando shards!
				// Como shards podem ser atualizadas sem depender de outras...
				val shards = botInfo.shardCount.filter { shardCount >= it.id }.toMutableList()
				val currentShard = botInfo.shardCount.firstOrNull { it.id == shardId } ?: run {
					val newShard = BotShard(shardId, guildCount)
					shards.add(newShard)
					newShard
				}
				currentShard.guildCount = guildCount
				GabrielaLauncher.gabriela.collection.updateOne(
						Filters.eq("_id", botId),
						Updates.set("shardCount", shards)
				)
			} else {
				// Não usando shards!
				GabrielaLauncher.gabriela.collection.updateOne(
						Filters.eq("_id", botId),
						Updates.combine(
								Updates.set("shardCount", listOf<BotShard>()),
								Updates.set("guildCount", guildCount)
						)
				)
			}
		} else {
			res.status(Status.FORBIDDEN)
			res.send(
					WebsiteUtils.createErrorPayload(
							GabiWebCode.FORBIDDEN,
							"Missing guildCount!"
					)
			)
			return
		}

		res.status(Status.NO_CONTENT)
		res.send("")
	}
}