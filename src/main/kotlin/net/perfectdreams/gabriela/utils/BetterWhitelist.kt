package net.perfectdreams.gabriela.utils

import org.jsoup.nodes.Attribute
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist
import java.util.regex.Pattern

class BetterWhitelist : Whitelist() {
	init {
		//copied from Whitelist.relaxed()
		addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
				"colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
				"i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
				"sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
				"ul", "iframe")
		addAttributes("a", "href", "title");
		addAttributes("blockquote", "cite");
		addAttributes("col", "span", "width");
		addAttributes("colgroup", "span", "width");
		addAttributes("img", "align", "alt", "height", "src", "title", "width");
		addAttributes("ol", "start", "type");
		addAttributes("q", "cite");
		addAttributes("table", "summary", "width");
		addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
		addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width");
		addAttributes("ul", "type")
		addAttributes("iframe", "width", "height", "src")
		addProtocols("a", "href", "ftp", "http", "https", "mailto");
		addProtocols("blockquote", "cite", "http", "https");
		addProtocols("cite", "cite", "http", "https");
		addProtocols("img", "src", "http", "https");
		addProtocols("q", "cite", "http", "https");
	}

	override fun isSafeAttribute(tagName: String, el: Element, attr: Attribute): Boolean {
		if (attr.key == "style") {
			// Apenas alguns atributos são considerados "seguros" para nós
			val styleValue = attr.value
			// CSS é uma map de key: value
			val safeStyleTags = mutableListOf<String>()
			val stylePattern = Pattern.compile("([A-z-]+):[ ]+?([A-z-0-9]+)")
			val styleMatcher = stylePattern.matcher(styleValue)
			while (styleMatcher.find()) {
				val key = styleMatcher.group(1)
				val value = styleMatcher.group(2)

				if (key == "text-align") {
					safeStyleTags.add("text-align: $value")
				}
			}
			if (safeStyleTags.isEmpty()) {
				attr.setValue(null)
			} else {
				attr.setValue(safeStyleTags.joinToString(";") + ";")
			}
			return true
		}
		return super.isSafeAttribute(tagName, el, attr)
	}
}