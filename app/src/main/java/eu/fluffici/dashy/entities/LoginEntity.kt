package eu.fluffici.dashy.entities

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.serialization.Serializable

data class LoginEntity(
    val error: String?,
    val message: String?,
    val status: Boolean,
    val token: String,
    val user: PartialUser
)

@Serializable
data class PartialUser(
    val username: String,
    val email: String,
    val avatar: Int,
    val avatarId: String?,
    val roles: String,
    val language: String?
) {
    fun toJSON(): JsonObject {
        val data = JsonObject()
        data.addProperty("username", this.username)
        data.addProperty("email", this.email)
        data.addProperty("avatar", this.avatar)
        data.addProperty("avatarId", this.avatarId)
        data.addProperty("roles", this.roles)
        data.addProperty("language", this.language)
        return data;
    }
}

data class PartialAuth(
    val pinCode: String
) {
    fun toJSON(): JsonObject {
        val data = JsonObject()
        data.addProperty("pinCode", this.pinCode)
        return data;
    }
}