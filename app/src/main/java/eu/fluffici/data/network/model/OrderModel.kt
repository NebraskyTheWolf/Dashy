package eu.fluffici.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderModel(
    val id: Int,
    val order_id: String?,
    val first_name: String?,
    val last_name: String?,
    val first_address: String?,
    val second_address: String?,
    val postal_code: String?,
    val country: String?,
    val email: String?,
    val phone_number: String?,
    val status: String?,
    val customer_id: String?,
    val created_at: String?,
    val updated_at: String?,
)
