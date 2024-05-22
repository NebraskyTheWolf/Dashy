package eu.fluffici.dashy.model

import kotlinx.serialization.Serializable

@Serializable
data class AuditModel(
    val id: Int,
    var name: String,
    var type: String,
    var slug: String,
    var created_at: String,
    var updated_at: String,
)