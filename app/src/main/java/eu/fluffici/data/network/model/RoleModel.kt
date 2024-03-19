package eu.fluffici.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleModel(
    val user: UserModel,
    val roles: List<Role>,
    val terminated: Boolean
)

@Serializable
data class Role(
    val id: Int,
    val slug: String,
    val name: String,
    val permissions: Map<String, String>,
    val created_at: String,
    val updated_at: String,
    val pivot: Pivot
)
@Serializable

data class Pivot(
    val user_id: Int,
    val role_id: Int
)

fun hasRole(roles: List<Role>, slug: String): Boolean {
    roles.forEach {
        if (it.slug == slug) {
            return true
        }
    }

    return false
}