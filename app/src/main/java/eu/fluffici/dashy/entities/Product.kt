package eu.fluffici.dashy.entities

import com.google.gson.JsonObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
data class Product(
    val product_name: String,
    val price: Int,
    val quantity: Int,
)

@Serializable
data class ProductBody(
    val id: Int,
    var price: Double,
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
    val createdAt: String,
    @SerialName("upc_code")
    val upcCode: String?,
    @SerialName("has_upc")
    val hasUpc: Int?,
    val solds: Int?,
    val quantity: Int,
) {
    fun toDeleteJSON() : RequestBody {
        val data = JsonObject();

        data.addProperty("id", id)

        return data.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun toUpdateJSON() : RequestBody {
        val data = JsonObject();

        data.addProperty("name", name)
        data.addProperty("description", description)
        data.addProperty("category_id", categoryId)
        data.addProperty("price", price)
        data.addProperty("displayed", (displayed == 1))

        return data.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }



}

data class ProductInventory(
    val productId: String,
    val quantity: Int
)