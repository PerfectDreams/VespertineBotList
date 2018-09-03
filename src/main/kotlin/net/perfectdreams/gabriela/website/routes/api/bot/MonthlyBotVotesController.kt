package net.perfectdreams.gabriela.website.routes.api.bot

import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mongodb.client.model.Filters
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.utils.GabiWebCode
import net.perfectdreams.gabriela.utils.WebsiteUtils
import org.bson.internal.Base64
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.GET
import org.jooby.mvc.Path
import javax.crypto.spec.SecretKeySpec

@Path("/api/v1/bot/:botId/monthly_votes")
class MonthlyBotVotesController {
	@GET
	fun updateStats(req: Request, res: Response) {
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

		val validVotes = botInfo.votes.filter { it.votedAt > System.currentTimeMillis() - 2592000000 }
		val array = JsonArray()
		validVotes.forEach {
			val obj = JsonObject()
			obj["id"] = it.id
			obj["votedAt"] = it.votedAt
			array.add(obj)
		}
		res.send(array.toString())
	}
}