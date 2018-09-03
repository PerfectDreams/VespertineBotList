package net.perfectdreams.gabriela.commands

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.perfectdreams.gabriela.commands.annotation.ArgumentType
import net.perfectdreams.gabriela.commands.annotation.InjectArgument
import net.perfectdreams.gabriela.commands.annotation.Subcommand
import net.perfectdreams.gabriela.utils.Constants
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

abstract class AbstractCommand(val label: String, val aliases: List<String> = listOf()) {
	fun matches(event: MessageReceivedEvent): Boolean {
		val message = event.message.contentDisplay

		val args = message.split(" ").toMutableList()
		val command = args[0]
		args.removeAt(0)

		val labels = mutableListOf(label)
		labels.addAll(aliases)

		val valid = labels.any { command == Constants.PREFIX + it }

		if (!valid)
			return false

		event.channel.sendTyping().complete()

		run(CommandContext(event))
		return true
	}

	fun run(context: CommandContext) {
		val args = context.args.toTypedArray()
		val baseClass = this::class.java

		// Ao executar, nós iremos pegar várias anotações para ver o que devemos fazer agora
		val methods = this::class.java.methods.filter { it.name != "matches" && it.name != "run" }

		for (method in methods.filter { it.isAnnotationPresent(Subcommand::class.java) }.sortedByDescending { it.parameterCount }) {
			val subcommandAnnotation = method.getAnnotation(Subcommand::class.java)
			val values = subcommandAnnotation.values
			for (value in values.map { it.split(" ") }) {
				var matchedCount = 0
				for ((index, text) in value.withIndex()) {
					val arg = args.getOrNull(index)
					if (text == arg)
						matchedCount++
				}
				val matched = matchedCount == value.size
				if (matched) {
					if (executeMethod(baseClass, method, context, context.event.message, "g!" /* TODO: Corrigir isto */, args ,matchedCount))
						return
				}
			}
		}

		// Nenhum comando foi executado... #chateado
		for (method in methods.filter { it.isAnnotationPresent(Subcommand::class.java) }.sortedByDescending { it.parameterCount }) {
			val subcommandAnnotation = method.getAnnotation(Subcommand::class.java)
			if (subcommandAnnotation.values.isEmpty()) {
				if (executeMethod(baseClass, method, context, context.event.message, "g!" /* TODO: Corrigir isto */, args, 0))
					return
			}
		}
		return
	}

	fun executeMethod(baseClass: Class<out AbstractCommand>, method: Method, context: CommandContext, message: Message, commandLabel: String, args: Array<String>, skipArgs: Int): Boolean {
		// check method arguments
		val arguments = args.toMutableList()
		for (i in 0 until skipArgs)
			arguments.removeAt(0)

		val params = getContextualArgumentList(method, context, message.author, commandLabel, arguments)

		// Agora iremos "validar" o argument list antes de executar
		for ((index, parameter) in method.kotlinFunction!!.valueParameters.withIndex()) {
			if (!parameter.type.isMarkedNullable && params[index] == null)
				return false
		}

		if (params.size != method.parameterCount)
			return false

		try {
			method.invoke(this, *params.toTypedArray())
		} catch (e: InvocationTargetException) {
			val targetException = e.targetException
			if (targetException is ExecutedCommandException) {
				message.channel.sendMessage(e.message ?: "Algo de errado aconteceu ao usar o comando...").queue()
			} else {
				throw e
			}
		}
		return true
	}

	fun getContextualArgumentList(method: Method, context: CommandContext, sender: User, commandLabel: String, arguments: MutableList<String>): List<Any?> {
		var dynamicArgIdx = 0
		val params = mutableListOf<Any?>()

		for ((index, param) in method.parameters.withIndex()) {
			val typeName = param.type.simpleName.toLowerCase()
			val injectArgumentAnnotation = param.getAnnotation(InjectArgument::class.java)
			when {
				injectArgumentAnnotation != null && injectArgumentAnnotation.type == ArgumentType.COMMAND_LABEL -> {
					params.add(commandLabel)
				}
				injectArgumentAnnotation != null && injectArgumentAnnotation.type == ArgumentType.ARGUMENT_LIST -> {
					params.add(arguments.joinToString(" "))
				}
				param.type.isAssignableFrom(Member::class.java) && sender is Member -> { params.add(sender) }
				param.type.isAssignableFrom(User::class.java) && sender is User -> { params.add(sender) }
				param.type.isAssignableFrom(String::class.java) -> {
					params.add(arguments.getOrNull(dynamicArgIdx))
					dynamicArgIdx++
				}
				param.type.isAssignableFrom(CommandContext::class.java) -> {
					params.add(context)
				}
			// Sim, é necessário usar os nomes assim, já que podem ser tipos primitivos ou objetos
				typeName == "int" || typeName == "integer" -> {
					params.add(arguments.getOrNull(dynamicArgIdx)?.toIntOrNull())
					dynamicArgIdx++
				}
				typeName == "double" -> {
					params.add(arguments.getOrNull(dynamicArgIdx)?.toDoubleOrNull())
					dynamicArgIdx++
				}
				typeName == "float" -> {
					params.add(arguments.getOrNull(dynamicArgIdx)?.toFloatOrNull())
					dynamicArgIdx++
				}
				typeName == "long" -> {
					params.add(arguments.getOrNull(dynamicArgIdx)?.toLongOrNull())
					dynamicArgIdx++
				}
				param.type.isAssignableFrom(Array<String>::class.java) -> {
					params.add(arguments.subList(dynamicArgIdx, arguments.size).toTypedArray())
				}
				param.type.isAssignableFrom(Array<Int?>::class.java) -> {
					params.add(arguments.subList(dynamicArgIdx, arguments.size).map { it.toIntOrNull() }.toTypedArray())
				}
				param.type.isAssignableFrom(Array<Double?>::class.java) -> {
					params.add(arguments.subList(dynamicArgIdx, arguments.size).map { it.toDoubleOrNull() }.toTypedArray())
				}
				param.type.isAssignableFrom(Array<Float?>::class.java) -> {
					params.add(arguments.subList(dynamicArgIdx, arguments.size).map { it.toFloatOrNull() }.toTypedArray())
				}
				param.type.isAssignableFrom(Array<Long?>::class.java) -> {
					params.add(arguments.subList(dynamicArgIdx, arguments.size).map { it.toLongOrNull() }.toTypedArray())
				}
				else -> params.add(null)
			}
		}
		return params
	}
}