package eu.fluffici.dashy.entities

import com.google.gson.JsonObject

data class LoginEntity(
    val error: String?,
    val message: String?,
    val status: Boolean,
    val token: String,
    val user: PartialUser
)


data class PartialUser(
    val username: String,
    val email: String,
    val avatar: Number,
    val avatarId: String?,
    val roles: String,
) {
    fun toJSON(): JsonObject {
        val data = JsonObject()
        data.addProperty("username", this.username)
        data.addProperty("email", this.email)
        data.addProperty("avatar", this.avatar)
        data.addProperty("avatarId", this.avatarId)
        data.addProperty("roles", this.roles)
        return data;
    }
}