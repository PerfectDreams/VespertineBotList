package net.perfectdreams.gabriela.utils

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.TextChannel
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela
import java.util.*

object Constants {
	const val GUILD_ID = "481252308110409748"
	const val BOT_ANALYSIS_GUILD_ID = "483270554200047626"
	const val BOT_ANALYSIS_BROADCAST_CHANNEL_ID = "483271000314871809"
	const val PREFIX = "g!"
	const val AUTHORIZATION_URL = "https://discordapp.com/oauth2/authorize?redirect_uri=https://bots.perfectdreams.net%2Flogin&scope=identify%20guilds%20email%20guilds.join&response_type=code&client_id=481901252007952385"
	const val LEFT_PADDING = "\uD83D\uDD39"
	const val DEFAULT_DESCRIPTION = "Apenas mais outro bot para o Discord."
	const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0"
	const val VERIFY_ROLE = "483287091036291073"
	val gson = Gson()
	val jsonParser = JsonParser()
	val random = SplittableRandom()
	val slowRandom = Random()
	val parser = Parser.builder(MutableDataSet()).build()
	val renderer = HtmlRenderer.builder(MutableDataSet()).build()

	val WEBSITE_URL: String by lazy {
		gabriela.config.websiteUrl
	}

	val botListGuild: Guild by lazy {
		gabriela.jda.getGuildById(GUILD_ID)
	}

	val botAnalysisGuild: Guild by lazy {
		gabriela.jda.getGuildById(BOT_ANALYSIS_GUILD_ID)
	}

	val verifyRole: Role by lazy {
		botListGuild.getRoleById(VERIFY_ROLE)
	}

	val botAnalysisBroadcastChannel: TextChannel by lazy {
		botAnalysisGuild.getTextChannelById(BOT_ANALYSIS_BROADCAST_CHANNEL_ID)
	}
}