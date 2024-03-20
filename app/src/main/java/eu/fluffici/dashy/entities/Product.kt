package eu.fluffici.dashy.entities

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val product_name: String,
    val price: Int,
    val quantity: Int,
)