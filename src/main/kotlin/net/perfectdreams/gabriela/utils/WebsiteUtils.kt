package net.perfectdreams.gabriela.utils

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonObject
import net.perfectdreams.gabriela.GabrielaLauncher.gabriela

object WebsiteUtils {
	/**
	 * Creates an JSON object wrapping the error object
	 *
	 * @param code    the error code
	 * @param message the error reason
	 * @return        the json object containing the error
	 */
	fun createErrorPayload(code: GabiWebCode, message: String? = null): JsonObject {
		return jsonObject("error" to createErrorObject(code, message))
	}

	/**
	 * Creates an JSON object containing the code error
	 *
	 * @param code    the error code
	 * @param message the error reason
	 * @return        the json object with the error
	 */
	fun createErrorObject(code: GabiWebCode, message: String? = null): JsonObject {
		val jsonObject = jsonObject(
				"code" to code.errorId,
				"reason" to code.fancyName,
				"help" to "${gabriela.config.websiteUrl}/docs/api"
		)

		if (message != null) {
			jsonObject["message"] = message
		}

		return jsonObject
	}
}