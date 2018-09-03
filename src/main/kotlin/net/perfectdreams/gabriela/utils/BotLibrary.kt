package net.perfectdreams.gabriela.utils

enum class BotLibrary(val title: String, val language: ProgrammingLanguage) {
	DISCORDCR("discordcr", ProgrammingLanguage.CRYSTAL),
	DISCORD_NET("Discord.Net", ProgrammingLanguage.C_SHARP),
	D_SHARP_PLUS("DSharpPlus", ProgrammingLanguage.C_SHARP),
	DSCORD("dscord", ProgrammingLanguage.D),
	DISCORD_GO("DiscordGo", ProgrammingLanguage.GO),
	DISCORD4J("Discord4J", ProgrammingLanguage.JAVA),
	JAVACORD("Javacord", ProgrammingLanguage.JAVA),
	JDA("JDA", ProgrammingLanguage.JAVA), // <3
	DISCORD_JS("discord.js", ProgrammingLanguage.JAVASCRIPT),
	ERIS("Eris", ProgrammingLanguage.ERIS),
	DISCORDIA("Discordia", ProgrammingLanguage.LUA),
	RESTCORD("RestCord", ProgrammingLanguage.PHP),
	YASMIN("Yasmin", ProgrammingLanguage.PHP),
	DISCORD_PY("discord.py", ProgrammingLanguage.PYTHON),
	DISCO("disco", ProgrammingLanguage.PYTHON),
	DISCORDRB("discordrb", ProgrammingLanguage.RUBY),
	DISCORD_RS("discord-rs", ProgrammingLanguage.RUST),
	SWORD("Sword", ProgrammingLanguage.SWIFT),
	UNKNOWN("Outra", ProgrammingLanguage.UNKNOWN)
}