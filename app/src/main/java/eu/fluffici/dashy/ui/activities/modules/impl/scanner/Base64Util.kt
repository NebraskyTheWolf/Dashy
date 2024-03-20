package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import java.util.regex.Matcher
import java.util.regex.Pattern

fun isBase64(s: String): Boolean {
    val pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"
    val r: Pattern = Pattern.compile(pattern)
    val m: Matcher = r.matcher(s)
    return m.find()
}