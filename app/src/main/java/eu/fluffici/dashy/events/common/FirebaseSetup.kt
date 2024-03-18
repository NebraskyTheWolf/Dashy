package eu.fluffici.dashy.events.common

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class FirebaseSetup(val token: String) {
    fun toJSON(): RequestBody {
        val data = JSONObject()
        data.put("token", this.token)

        return data.toString().toRequestBody("application/json".toMediaType())
    }
}
