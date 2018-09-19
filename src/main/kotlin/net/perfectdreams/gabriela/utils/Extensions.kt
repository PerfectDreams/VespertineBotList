package net.perfectdreams.gabriela.utils

import kotlinx.html.*
import net.dv8tion.jda.core.OnlineStatus
import net.perfectdreams.gabriela.models.DiscordBot
import net.perfectdreams.gabriela.utils.Constants.random
import org.jooby.Request
import java.awt.Color
import java.net.URLEncoder

fun Color.toKotlinCss() = kotlinx.css.Color("rgb(${this.red}, ${this.green}, ${this.blue})")

fun DIV.generateAd() {
	unsafe {
		raw("""
			<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- VespertineBotList -->
<ins class="adsbygoogle"
     style="display:block"
     data-ad-client="ca-pub-9989170954243288"
     data-ad-slot="8330683640"
     data-ad-format="auto"
     data-full-width-responsive="true"></ins>
<script>
(adsbygoogle = window.adsbygoogle || []).push({});
</script>
		""".trimIndent())
	}
}

/**
 * Returns the request "true IP"
 * If the "X-Forwarded-For" header is set, then the value of that header is used, if not, Jooby's [Request.ip()] is used
 */
val Request.trueIp: String get() {
	val forwardedForHeader = this.header("X-Forwarded-For")
	return if (forwardedForHeader.isSet) {
		val ips = forwardedForHeader.value() // Cloudflare, Apache
		return ips.split(", ")[0]
	} else
		this.ip()
}

/**
 * Returns the query strings as used in URLs (prefixed with "?")
 */
val Request.urlQueryString: String get() {
	return if (this.queryString().isPresent) {
		"?" + this.queryString().get()
	} else {
		""
	}
}

fun String.encodeToUrl(enc: String = "UTF-8"): String {
	return URLEncoder.encode(this, enc)
}

fun <T> List<T>.getRandom(): T {
	return this[random.nextInt(this.size)]
}

fun DIV.generateHeader(icon: String, title: String, subtitle: String) {
	div("header-wrapper") {
		i(classes = "$icon header-icon blurple-header")
		div("header-inner") {
			h1(classes = "header-title") {
				+" $title"
			}
			div(classes = "header-subtitle") {
				+ subtitle
			}
		}
	}
}

fun DIV.generateHeader(icon: ImageIcon, title: String, subtitle: String) {
	div("header-wrapper") {
		img(src = icon.url, classes = "rounded-image") {
			width = "64"
			height = "64"
		}
		i(classes = "$icon header-icon blurple-header")
		div("header-inner") {
			h1(classes = "header-title") {
				+" $title"
			}
			div(classes = "header-subtitle") {
				+ subtitle
			}
		}
	}
}

fun DIV.generateBotInfo(bot: DiscordBot, customLinks: (DIV.() -> Unit)? = null) {
	val status = DiscordUtils.getUserStatus(bot.botId)
	div("pure-u-1 pure-u-md-1-3") {
		div {
			id = "bot-entry"
			classes += "color-${bot.color.name.toLowerCase().replace("_", "-")}"
			if (status == OnlineStatus.OFFLINE)
				style = "opacity: 0.5;"
			div(classes = "header") {
				div(classes = "status") {
					classes += when (status) {
						OnlineStatus.ONLINE -> "online-color"
						OnlineStatus.IDLE -> "away-color"
						OnlineStatus.DO_NOT_DISTURB -> "busy-color"
						else -> "offline-color"
					}
				}
				img(classes = "rounded-image avatar", src = DiscordUtils.getUserAvatarUrl(bot.botId))
				div(classes = "info-wrapper") {
					div(classes = "info") {
						div(classes = "name") {
							+ DiscordUtils.getUserName(bot.botId)
						}
						if (bot.getServerCount() != 0) {
							div(classes = "guild-count") {
								+"${bot.getServerCount()} servidores"
							}
						}
					}
					div(classes = "upvote") {
						i(classes = "far fa-thumbs-up")
						span(classes = "bot-upvotes") {
							+ " ${bot.votes.count { it.votedAt > System.currentTimeMillis() - 2592000000}}"
						}
					}
				}
			}
			div(classes = "description") {
				div {
					+ (bot.tagline ?: Constants.DEFAULT_DESCRIPTION)
				}
				if (bot.categories.isNotEmpty()) {
					div(classes = "tags") {
						i(classes = "fas fa-tags")
						+" "
						for (category in bot.categories) {
							span {
								+ category.title
							}
							if (bot.categories.last() != category) {
								+", "
							}
						}
					}
				}
				hr {}
				div(classes = "more-info") {
					if (customLinks != null) {
						customLinks()
					} else {
						div("pure-g") {
							var count = 2
							if (!bot.websiteUrl.isNullOrBlank()) {
								count++
							}
							div("pure-u-1 pure-u-md-1-$count") {
								div(classes = "more-info-button") {
									a(href="${Constants.WEBSITE_URL}/bot/${bot.botId}") {
										+ "Ver"
									}
								}
							}
							if (!bot.websiteUrl.isNullOrBlank()) {
								div("pure-u-1 pure-u-md-1-$count") {
									div(classes = "more-info-button") {
										a(href = "${Constants.WEBSITE_URL}/bot/${bot.botId}/website", target = "_blank") {
											+"Website"
										}
									}
								}
							}
							div("pure-u-1 pure-u-md-1-$count") {
								div(classes = "more-info-button") {
									a(href = "${Constants.WEBSITE_URL}/bot/${bot.botId}/invite") {
										+"Adicionar"
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

fun DIV.generateBotsInfo(bots: List<DiscordBot>, customLinks: (DIV.() -> Unit)? = null) {
	val row1 = bots.filterIndexed { index, discordBot -> index % 4 == 0 }
	val row2 = bots.filterIndexed { index, discordBot -> index % 4 == 1 }
	val row3 = bots.filterIndexed { index, discordBot -> index % 4 == 2 }
	val row4 = bots.filterIndexed { index, discordBot -> index % 4 == 3 }

	fun generateInfo(bot: DiscordBot) {
		val status = DiscordUtils.getUserStatus(bot.botId)
		div {
			id = "bot-entry"
			classes += "color-${bot.color.name.toLowerCase().replace("_", "-")}"
			if (status == OnlineStatus.OFFLINE)
				style = "opacity: 0.5;"
			div(classes = "header") {
				div(classes = "status") {
					classes += when (status) {
						OnlineStatus.ONLINE -> "online-color"
						OnlineStatus.IDLE -> "away-color"
						OnlineStatus.DO_NOT_DISTURB -> "busy-color"
						else -> "offline-color"
					}
				}
				img(classes = "rounded-image avatar", src = DiscordUtils.getUserAvatarUrl(bot.botId))
				div(classes = "info-wrapper") {
					div(classes = "info") {
						div(classes = "name") {
							+DiscordUtils.getUserName(bot.botId)
						}
						if (bot.getServerCount() != 0) {
							div(classes = "guild-count") {
								+"${bot.getServerCount()} servidores"
							}
						}
					}
					div(classes = "upvote") {
						i(classes = "far fa-thumbs-up")
						span(classes = "bot-upvotes") {
							+" ${bot.votes.count { it.votedAt > System.currentTimeMillis() - 2592000000 }}"
						}
					}
				}
			}
			div(classes = "description") {
				div(classes = "description-text") {
					+(bot.tagline ?: Constants.DEFAULT_DESCRIPTION)
				}
				if (bot.categories.isNotEmpty()) {
					div(classes = "tags") {
						i(classes = "fas fa-tags")
						+" "
						for (category in bot.categories) {
							span {
								+category.title
							}
							if (bot.categories.last() != category) {
								+", "
							}
						}
					}
				}
				hr {}
				div(classes = "more-info") {
					if (customLinks != null) {
						customLinks()
					} else {
						a(href = "${Constants.WEBSITE_URL}/bot/${bot.botId}") {
							+ "Ver"
						}
						a(href = "${Constants.WEBSITE_URL}/bot/${bot.botId}/invite") {
							+ "Adicionar"
						}
						if (!bot.websiteUrl.isNullOrBlank()) {
							a(href = "${Constants.WEBSITE_URL}/bot/${bot.botId}/website", target = "_blank") {
								+ "Website"
							}
						}
					}
				}
			}
		}
	}

	div("pure-u-1 pure-u-md-1-4") {
		for (bot in row1) {
			generateInfo(bot)
		}
	}
	div("pure-u-1 pure-u-md-1-4") {
		for (bot in row2) {
			generateInfo(bot)
		}
	}
	div("pure-u-1 pure-u-md-1-4") {
		for (bot in row3) {
			generateInfo(bot)
		}
	}
	div("pure-u-1 pure-u-md-1-4") {
		for (bot in row4) {
			generateInfo(bot)
		}
	}
}