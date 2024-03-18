package eu.fluffici.dashy.events.auth

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class LoginRequest(val email: String, val password: String, val rememberMe: Boolean) {
    fun toJSON(): RequestBody {
        val data = JSONObject()
        data.put("email", this.email)
        data.put("password", this.password)

        return data.toString().toRequestBody("application/json".toMediaType())
    }
}