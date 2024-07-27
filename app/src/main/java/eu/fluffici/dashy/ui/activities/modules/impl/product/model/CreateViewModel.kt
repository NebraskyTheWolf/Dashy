package eu.fluffici.dashy.ui.activities.modules.impl.product.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import eu.fluffici.dashy.entities.ProductBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

data class CDummyProduct(
    var upcCode: String,
    var name: String,
    var categoryId: Int,
    var description: String,
    var price: String,
    var status: String,
) {
    fun toJSON(): RequestBody {
        val data = JsonObject()

        data.addProperty("name", name)
        data.addProperty("description", description)
        data.addProperty("category_id", categoryId)
        data.addProperty("price", price.toDouble())
        data.addProperty("displayed", (status === "Public"))
        data.addProperty("upc_code", upcCode)
        data.addProperty("has_upc", true)

        return data.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }
}

data class DummyCategory(
    var id: Int,
    var name: String
)

data class DummyProductSale(
    var product: ProductBody,
    var reduction: String,
    var expiration: String
) {
    private fun toLaravelTime() : String {
        return expiration.replace(" ", "T").plus(".000000Z")
    }

    fun toJSON(): RequestBody {
        val data = JsonObject()

        data.addProperty("productId", product.id)
        data.addProperty("reduction", reduction.toDouble())
        data.addProperty("expiration", toLaravelTime())

        return data.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }
}

@SuppressWarnings("All")
class CreateViewModel @Inject constructor() : ViewModel() {
    private val client = OkHttpClient()
    private val gson = Gson()

    private val _categories = MutableStateFlow<List<DummyCategory>>(emptyList())
    val categories: StateFlow<List<DummyCategory>> get() = _categories

    private val _getErrors = MutableStateFlow(Pair(false, DummyModal("", "")))
    val getErrors: StateFlow<Pair<Boolean, DummyModal>> get() = _getErrors

    init {
        this.init()
    }

    private fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/device/product/categories")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .get()
                .build()

            val response = client.newCall(request).execute();
            if (response.isSuccessful) {
                val data = gson.fromJson(response.body?.string(), JsonObject::class.java)
                if (data.get("status").asBoolean) {
                    _categories.value = data.get("data").asJsonArray.toArrayList()
                } else {
                    _getErrors.value = Pair(true, DummyModal("Error", data.get("message").asString))
                }
            } else {
                _getErrors.value = Pair(true, DummyModal("Error", "Unable to contact Fluffici's server."))
            }
        }
    }

    fun createProduct(product: CDummyProduct): Boolean {
        val ref = AtomicBoolean(false)

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/device/product/create")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .post(product.toJSON())
                .build()

            val response = client.newCall(request).execute();
            ref.set(response.isSuccessful)
        }

        return ref.get()
    }

    fun deleteProduct(product: ProductBody): Boolean {
        val ref = AtomicBoolean(false)

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/device/product/delete")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .delete(product.toDeleteJSON())
                .build()
            val response = client.newCall(request).execute();
            ref.set(response.isSuccessful)
        }

        return ref.get()
    }

    fun updateProduct(product: ProductBody): Boolean {
        val ref = AtomicBoolean(false)

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/device/product/update")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .patch(product.toUpdateJSON())
                .build()
            val response = client.newCall(request).execute();
            ref.set(response.isSuccessful)
        }

        return ref.get()
    }

    fun createSale(sale: DummyProductSale): Boolean {
        val ref = AtomicBoolean(false)

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/device/product/create-sale")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .post(sale.toJSON())
                .build()
            val response = client.newCall(request).execute();
            ref.set(response.isSuccessful)
        }

        return ref.get()
    }
}

fun JsonArray.toArrayList(): List<DummyCategory> {
    return Gson().fromJson(this, object : TypeToken<List<DummyCategory>>() {}.type)
}