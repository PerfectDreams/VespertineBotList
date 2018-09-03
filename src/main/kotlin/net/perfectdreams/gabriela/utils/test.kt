package net.perfectdreams.gabriela.utils

import com.github.kevinsawicki.http.HttpRequest

fun main(args: Array<String>) {
	val http = HttpRequest.post("https://discordapp.com/api/webhooks/395688631613915136/KkN2nECWkkImsur3E6lkvksJ_NXc7PFXVlDNf-rFyjuTvf0pXrYidVqnjfrmekUktI6u")
			.userAgent("owo")
			.header("Content-Type", "multipart/form-data; boundary=---------------------------9051914041544843365972754266")
			// .part("content", "hello world!!!")
			.send("""-----------------------------9051914041544843365972754266
Content-Disposition: form-data; name="payload_json"
Content-Type: application/json

{ "content": "test" }
			""".trimIndent())


	println(http.body())
}