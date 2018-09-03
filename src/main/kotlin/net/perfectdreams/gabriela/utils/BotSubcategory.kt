package net.perfectdreams.gabriela.utils

enum class BotSubcategory(val title: String, val mainCategory: BotCategory) {
	// GAMING
	PUBG("PUBG", BotCategory.GAMING),
	ROCKET_LEAGUE("Rocket League", BotCategory.GAMING),
	FORTNITE("Fortnite", BotCategory.GAMING),
	OVERWATCH("Overwatch", BotCategory.GAMING),
	CSGO("CSGO", BotCategory.GAMING),
	OSU("OSU!", BotCategory.GAMING),
	LEAGUE_OF_LEGENDS("League of Legends", BotCategory.GAMING),
	WARFRAME("Warframe", BotCategory.GAMING),
	DIABLO_3("Diablo 3", BotCategory.GAMING),
	RUST("Rust", BotCategory.GAMING),
	DOTA_2("DOTA 2", BotCategory.GAMING),
	STARCRAFT_2("Starcraft 2", BotCategory.GAMING),
	FACTORIO("Factorio", BotCategory.GAMING),
	LOOKING_FOR_GAME("Looking for Game", BotCategory.GAMING),
	MINECRAFT("Minecraft", BotCategory.GAMING),
	ROBLOX("Roblox", BotCategory.GAMING),
	POKEMON("Pokémon", BotCategory.GAMING),

	// ROLEPLAY
	RPG("RPG", BotCategory.ROLEPLAY),

	// ENTERTAINMENT
	ENTERTAINMENT("Soundbound", BotCategory.ENTERTAINMENT),
	MEMES("Memes", BotCategory.ENTERTAINMENT),

	// ECONOMY
	GAMBLING("Apostas", BotCategory.ECONOMY),

	// ROLE_MANAGEMENT
	REGISTER("Registrar", BotCategory.ROLE_MANAGEMENT),

	// MODERATION
	ANTI_RAID("Anti Raid", BotCategory.MODERATION),
	AUTO_MODERATION("Auto Moderação", BotCategory.MODERATION),
	ANTI_INVITE_LINKS("Bloquear Convites", BotCategory.MODERATION)
}