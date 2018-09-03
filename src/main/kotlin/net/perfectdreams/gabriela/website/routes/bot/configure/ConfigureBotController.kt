package net.perfectdreams.gabriela.website.routes.bot.configure

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import net.perfectdreams.gabriela.GabrielaLauncher
import net.perfectdreams.gabriela.oauth2.TemmieDiscordAuth
import net.perfectdreams.gabriela.utils.*
import net.perfectdreams.gabriela.views.ConfigureBot
import net.perfectdreams.gabriela.views.GenericError
import net.perfectdreams.gabriela.views.GenericInfo
import org.jooby.Request
import org.jooby.Response
import org.jooby.Status
import org.jooby.mvc.GET
import org.jooby.mvc.POST
import org.jooby.mvc.Path

@Path("/bot/:botId/configure")
class ConfigureBotController {
	@GET
	fun showBotConfiguration(req: Request, res: Response) {
		val botId = req.param("botId")
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId.value())).firstOrNull()
		val bot = Constants.botListGuild.getMemberById(botId.value())
		val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
		val userIdentification = if (optional.isPresent) optional.get() else return

		if (botInfo != null && bot != null && (botInfo.ownerId == userIdentification.id || botInfo.ownerIds.contains(userIdentification.id))) {
			res.send(ConfigureBot.build(req, botInfo, bot))
			return
		}

		res.status(Status.FORBIDDEN)
		res.send(GenericError.build(req, "whoops"))
	}

	@POST
	fun saveBotConfiguration(req: Request, res: Response) {
		val type = req.param("type").value()
		val botId = req.param("botId").value()
		val optional = req.ifGet<TemmieDiscordAuth.UserIdentification>("userIdentification")
		val userIdentification = if (optional.isPresent) optional.get() else return
		val botInfo = GabrielaLauncher.gabriela.collection.find(Filters.eq("_id", botId)).firstOrNull()

		if (botInfo != null && (botInfo.ownerId == userIdentification.id || botInfo.ownerIds.contains(userIdentification.id))) {
			if (type == "update") {
				val form = req.form(ConfigureBotForm::class.java)

				val categoryNames = form.category ?: listOf()
				val subCategoryNames = form.subcategory ?: listOf()
				val categories = categoryNames.mapNotNull {
					try {
						BotCategory.valueOf(it)
					} catch (e: Exception) {
						null
					}
				}.toMutableList()
				while (categories.size > 5)
					categories.removeAt(0)
				val subCategories = subCategoryNames.mapNotNull {
					try {
						val subCategory = BotSubcategory.valueOf(it)
						if (categories.contains(subCategory.mainCategory))
							subCategory
						else
							null
					} catch (e: Exception) {
						null
					}
				}

				try {
					val library = BotLibrary.valueOf(form.botLibrary)
					val botColor = EntryColor.valueOf(form.botColor)
					GabrielaLauncher.gabriela.collection.updateOne(
							Filters.eq("_id", botInfo.botId),
							Updates.combine(
									Updates.set("library", library.name),
									Updates.set("prefix", form.prefix),
									Updates.set("tagline", form.tagline),
									Updates.set("color", botColor.name),
									Updates.set("description", form.description),
									Updates.set("categories", categories.map { it.name }),
									Updates.set("subCategories", subCategories.map { it.name }),
									Updates.set("websiteUrl", form.websiteUrl),
									Updates.set("inviteUrl", form.inviteUrl),
									Updates.set("repositoryUrl", form.repositoryUrl),
									Updates.set("supportUrl", form.supportUrl)
							)
					)
				} catch (e: Exception) {
					res.status(Status.SERVER_ERROR)
					res.send(GenericError.build(req, "Erro ao salvar a configuração do seu bot!"))
				}

				res.send(GenericInfo.build(req, "Sucesso!"))
				return
			}
			if (type == "reset") {
				botInfo.generateNewToken()
				GabrielaLauncher.gabriela.collection.updateOne(
						Filters.eq("_id", botInfo.botId),
						Updates.set("token", botInfo.token)
				)
				res.send(GenericInfo.build(req, "Token alterado com sucesso!"))
				return
			}
			if (type == "promote") {
				if (botInfo.lastBump + 14_400_000 > System.currentTimeMillis()) {
					res.send(GenericInfo.build(req, "Ainda não se passaram quatro horas desde a última promoção!"))
				} else {
					GabrielaLauncher.gabriela.collection.updateOne(
							Filters.eq("_id", botInfo.botId),
							Updates.set("lastBump", System.currentTimeMillis())
					)
					res.send(GenericInfo.build(req, "Seu bot foi promovido com sucesso!"))
				}
			}
			res.status(Status.FORBIDDEN)
			res.send(GenericError.build(req, "whoops"))
		}
	}

	class ConfigureBotForm(
			val botId: String,
			val botLibrary: String,
			val prefix: String,
			val botColor: String,
			val tagline: String,
			val description: String,
			val inviteUrl: String,
			val websiteUrl: String,
			val supportUrl: String,
			val repositoryUrl: String,
			val category: List<String>?,
			val subcategory: List<String>?
	)
}