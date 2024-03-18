package eu.fluffici.dashy.entities

import com.google.gson.JsonObject

data class PermissionEntity(
    val permission: String,
    val isGranted: Boolean,
    val error: String?,
    val message: String?,
) {
    fun toJSON(): JsonObject {
        val data = JsonObject()
        data.addProperty("permission", this.permission)
        data.addProperty("isGranted", this.isGranted)
        return data;
    }
}