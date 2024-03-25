package eu.fluffici.dashy.entities

import kotlinx.serialization.Serializable

@Serializable
data class Voucher(
    val balance: Int,
    val isExpired: Boolean,
    val isRestricted: Boolean,
    val customer: Customer,
    val expireAt: String,
)
@Serializable
data class Customer(
    val first_name: String,
    val last_name: String,
    val email: String
)


@Serializable
data class Error(
    val status: Boolean,
    val error: String,
    val message: String
)