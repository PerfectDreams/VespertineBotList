package net.perfectdreams.gabriela.utils

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.*
import net.dv8tion.jda.core.OnlineStatus
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.Constants.botListGuild
import org.json.XML
import java.net.InetAddress

object DiscordUtils {
	fun getUserAvatarUrl(userId: String): String {
		return gabriela.jda.getUserById(userId)?.effectiveAvatarUrl ?: gabriela.jda.users.first().defaultAvatarUrl
	}

	fun getUserName(userId: String): String {
		return gabriela.jda.getUserById(userId)?.name ?: "???"
	}

	fun getUserStatus(userId: String): OnlineStatus {
		val user =  gabriela.jda.getUserById(userId) ?: return OnlineStatus.UNKNOWN
		return botListGuild.getMember(user)?.onlineStatus ?: OnlineStatus.UNKNOWN
	}

	fun verifyAccount(userIdentification: TemmieDiscordAuth.UserIdentification, ip: String): AccountCheckResult {
		if (!userIdentification.verified)
			return AccountCheckResult.NOT_VERIFIED

		val email = userIdentification.email ?: return AccountCheckResult.NOT_VERIFIED // Sem email == não verificado (?)

		val domain = email.split("@")
		if (2 > domain.size) // na verdade seria "INVALID_EMAIL" mas...
			return AccountCheckResult.NOT_VERIFIED

		val list = HttpRequest.get("https://raw.githubusercontent.com/martenson/disposable-email-domains/master/disposable_email_blacklist.conf")
				.body()
				.split("\n")
				.toMutableList()

		// Alguns emails que não estão na lista
		list.add("sparklmail.com")
		list.add("l8oaypr.com")

		// mailto.space
		try {
			val body = HttpRequest.get("https://mailto.space/get/inbox/c785304469fbf265b6c71965f194e653e4c4951c/wbydvhbby")
					.userAgent(Constants.USER_AGENT)
					.body()

			val element = Constants.jsonParser.parse(body)

			val array = element.array
			val domainsArray = array[1].array

			list.addAll(domainsArray.map { it.string })
		} catch (e: Exception) {
			// logger.error("Erro ao tentar pegar email atual do mailto.space!", e)
		}

		val matches = list.any { it == domain[1] }

		if (matches)
			return AccountCheckResult.BAD_EMAIL

		return verifyIP(ip)
	}

	fun verifyIP(ip: String): AccountCheckResult {
		// Para identificar meliantes, cada request terá uma razão determinando porque o IP foi bloqueado
		// 0 = Stop Forum Spam
		// 1 = Bad hostname
		// 2 = OVH IP

		// Antes de nós realmente decidir "ele deu upvote então vamos dar o upvote", nós iremos verificar o IP no StopForumSpam
		val stopForumSpam = HttpRequest.get("http://api.stopforumspam.org/api?ip=$ip")
				.body()

		// STOP FORUM SPAM
		val xmlJSONObj = XML.toJSONObject(stopForumSpam)

		val response = Constants.jsonParser.parse(xmlJSONObj.toString(4)).obj["response"]

		val isSpam = response["appears"].bool

		if (isSpam)
			return AccountCheckResult.STOP_FORUM_SPAM

		// HOSTNAME BLOCC:tm:
		val addr = InetAddress.getByName(ip)
		val host = addr.hostName.toLowerCase()

		val hostnames = listOf(
				"anchorfree", // Hotspot Shield
				"ipredator.se", // IP redator
				"pixelfucker.org", // Pixelfucker
				"theremailer.net", // TheRemailer
				"tor-exit", // Tor Exit
				"torexit",
				"exitpoint"
		)

		val badHostname = hostnames.any { host.contains(it) }

		if (badHostname)
			return AccountCheckResult.BAD_HOSTNAME

		// OVH BLOCC:tm:
		if (host.matches(Regex(".*ns[0-9]+.*")))
			return AccountCheckResult.OVH_HOSTNAME

		return AccountCheckResult.SUCCESS
	}

	enum class AccountCheckResult(val canAccess: Boolean) {
		SUCCESS(true),
		NOT_VERIFIED(false),
		BAD_EMAIL(false),
		STOP_FORUM_SPAM(false),
		BAD_HOSTNAME(false),
		OVH_HOSTNAME(false)
	}
}