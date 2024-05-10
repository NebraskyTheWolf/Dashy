package eu.fluffici.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val avatar: Int,
    val avatar_id: String?,
    val created_at: String,
    val updated_at: String,
    val fcm_token: String?,
    val is_fcm: Boolean,
    val language: String,
    val deleted_at: String?,
    val bio: String?,
    val pronouns: String?
)