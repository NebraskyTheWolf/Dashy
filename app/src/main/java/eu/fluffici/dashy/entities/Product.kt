package eu.fluffici.dashy.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val product_name: String,
    val price: Int,
    val quantity: Int,
)

@Serializable
data class ProductBody(
    val id: Int,
    val price: Int,
    val views: Int,
    val name: String,
    @SerialName("category_id")
    val categoryId: Int,
    val displayed: Int,
    @SerialName("image_path")
    val productIcon: String?,
    val description: String,

    @SerialName("deleted_at")
    val deletedAt: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String
)

data class ProductInventory(
    val productId: String,
    val quantity: Int
)