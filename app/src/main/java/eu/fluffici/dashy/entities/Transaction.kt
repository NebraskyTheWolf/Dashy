package eu.fluffici.dashy.entities

import kotlinx.serialization.Serializable
@Serializable
data class Transaction(
    val order_id: String?,
    val status: String?,
    val transaction_id: String?,
    val provider: String?,
    val price: Int?,
)