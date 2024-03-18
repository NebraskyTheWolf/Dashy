package eu.fluffici.dashy.utils

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

object Validation {
    private val strictAdapter: TypeAdapter<JsonElement> = Gson().getAdapter(JsonElement::class.java)
    fun isValid(json: String): Boolean {
        try {
            strictAdapter.fromJson(json);
        } catch (e: Exception) {
            return false;
        }
        return true;
    }

    fun isBase64Encoded(s: String): Boolean {
        val pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$"
        val r: Pattern = Pattern.compile(pattern)
        val m: Matcher = r.matcher(s)
        return m.find()
    }
}