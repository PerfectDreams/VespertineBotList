package net.perfectdreams.gabriela.utils

fun main(args: Array<String>) {
	for (color in EntryColor.values()) {
		val cssName = color.name.replace("_", "-").toLowerCase()

		println("#bot-entry.color-$cssName div.header { background-color: var(--color-$cssName-primary); }")
		println("#bot-entry.color-$cssName div.description { background-color: var(--color-$cssName-secondary); }")
		println("#bot-entry.color-$cssName div.status { border-color: var(--color-$cssName-primary); }")
		println(".color-$cssName #bot-info .guild-status { color: var(--color-$cssName-primary); }")
		println(".color-$cssName #bot-info .title { color: var(--color-$cssName-secondary); }")
		println(".color-$cssName #bot-info .button { background-color: var(--color-$cssName-secondary); color: white; }")
		println(".color-$cssName div.topnav { background-color: var(--color-$cssName-secondary); }")
		println(".color-$cssName footer { background-color: var(--color-$cssName-secondary); }")
		println("")
	}
}