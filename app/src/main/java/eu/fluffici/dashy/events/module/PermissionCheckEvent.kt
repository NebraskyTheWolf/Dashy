package eu.fluffici.dashy.events.module

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class PermissionCheckEvent(val permission: String) {
    fun toJSON(): RequestBody {
        val data = JSONObject()
        data.put("permission", this.permission)
        return data.toString().toRequestBody("application/json".toMediaType())
    }
}