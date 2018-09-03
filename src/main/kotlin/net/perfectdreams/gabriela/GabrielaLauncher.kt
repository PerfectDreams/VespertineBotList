package net.perfectdreams.gabriela

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import net.perfectdreams.gabriela.utils.GabrielaConfig
import java.io.File

object GabrielaLauncher {
	lateinit var gabriela: Gabriela
	val mapper = ObjectMapper(YAMLFactory())

	@JvmStatic
	fun main(args: Array<String>) {
		val config = mapper.readValue(File("config.yml"), GabrielaConfig::class.java)

		gabriela = Gabriela(config)
		gabriela.start()
	}
}