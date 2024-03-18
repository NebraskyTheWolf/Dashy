package eu.fluffici.dashy.events.auth

import org.json.JSONObject

data class OTPEvent(
    val otpCode: String?,
    val success: Boolean,
) {
    fun toJSON(): JSONObject {
        val data = JSONObject()
        data.put("code", this.otpCode.toString())

        println(data.toString())
        println(data.toString())
        println(data.toString())
        println(data.toString())

        return data
    }
}