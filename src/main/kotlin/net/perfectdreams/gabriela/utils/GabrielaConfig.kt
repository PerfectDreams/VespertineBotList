package net.perfectdreams.gabriela.utils

import com.fasterxml.jackson.annotation.JsonProperty

class GabrielaConfig(
		@JsonProperty("token")
		val token: String,
		@JsonProperty("client-id")
		val clientId: String,
		@JsonProperty("client-secret")
		val clientSecret: String,
		@JsonProperty("website-url")
		val websiteUrl: String,
		@JsonProperty("frontend-folder")
		val frontendFolder: String,
		@JsonProperty("mongodb-ip")
		val mongoDbIp: String,
		@JsonProperty("recaptcha-secret")
		val recaptchaSecret: String
)