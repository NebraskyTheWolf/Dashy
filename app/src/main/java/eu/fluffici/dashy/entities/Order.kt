package eu.fluffici.dashy.entities

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class Sale(
    val id: Int,
    val product_id: Int,
    val product_type: String,
    val reduction: Double,
    val deleted_at: String,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class Carrier(
    val id: Int,
    val slug: String,
    val carrierName: String,
    val carrierPrice: Double,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class Address(
    val id: Int,
    val customer_id: String,
    val address_one: String,
    val address_two: String,
    val city: String,
    val zip: String,
    val country: String,
    val type: String,
    val primary: Int,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class PartCustomer(
    val id: Int,
    val customer_id: String,
    val username: String,
    val first_name: String,
    val middle_name: String,
    val last_name: String,
    val phone: String,
    val email: String,
    val created_at: String,
    val updated_at: String
)

data class FullOrder(
    val order: Order,
    val customer: MutableState<PartCustomer>,
    val address: MutableState<Address>,
    val carrier: MutableState<Carrier>,
    val sale: MutableState<Sale>,
    val products: MutableState<List<Product>>,
    val transactions: MutableState<List<Transaction>>
)

@Serializable
data class Order(
    val id: Int,
    val order_id: String,
    val sale_id: Int,
    val carrier_id: Int,
    val address_id: Int,
    val status: String,
    val customer_id: String,
    val created_at: String,
    val updated_at: String,
    val tracking_number: String
) {
    private suspend fun fetchSale(): Sale = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/device/fetch-sale/$sale_id")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        return@withContext if (response.isSuccessful) {
            Gson().fromJson(response.body?.string(), Sale::class.java)
        } else {
            Sale(0, 0, "", 0.0, "", "", "")
        }
    }

    private suspend fun fetchCarrier(): Carrier = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/device/fetch-carrier/$carrier_id")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        return@withContext if (response.isSuccessful) {
            Gson().fromJson(response.body?.string(), Carrier::class.java)
        } else {
            Carrier(0, "", "", 0.0, "", "")
        }
    }

    private suspend fun fetchAddress(): Address = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/device/fetch-address/$address_id")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        return@withContext if (response.isSuccessful) {
            Gson().fromJson(response.body?.string(), Address::class.java)
        } else {
            Address(0, "", "", "", "", "", "", "", 0, "", "")
        }
    }

    private suspend fun fetchCustomer(): PartCustomer = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/device/fetch-customer/$customer_id")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        return@withContext if (response.isSuccessful) {
            Gson().fromJson(response.body?.string(), PartCustomer::class.java)
        } else {
            PartCustomer(0, "", "", "", "", "", "", "", "", "")
        }
    }

    private suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Product>()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/order?orderId=${order_id}")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            var data = Gson().fromJson(response.body?.string(), JsonObject::class.java)
            data = data.get("data").asJsonObject

            if (data.get("product").isJsonArray) {
                data.get("product").asJsonArray.forEach {
                    val product = it.asJsonObject

                    result.add(
                        Product(
                            product.get("name").asString,
                            product.get("price").asInt,
                            product.get("quantity").asInt,
                        )
                    )
                }
            }
        } else {
            Log.d("OrderManager", "Unable to fetch products from the remote server.")
        }

        return@withContext result
    }

    private suspend fun getTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Transaction>()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/order?orderId=${order_id}")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            var data = Gson().fromJson(response.body?.string(), JsonObject::class.java)
            data = data.get("data").asJsonObject

            if (data.get("payment").isJsonArray) {
                data.get("payment").asJsonArray.forEach {
                    val payment = it.asJsonObject

                    result.add(
                        Transaction(
                            order_id,
                            payment.get("status").asString,
                            payment.get("transaction_id").asString,
                            payment.get("provider").asString,
                            payment.get("price").asInt,
                        )
                    )
                }
            }
        } else {
            Log.d("OrderManager", "Unable to fetch products from the remote server.")
        }

        return@withContext result
    }

    suspend fun getAllDetails(): FullOrder = withContext(Dispatchers.IO) {
        return@withContext FullOrder(
            order = this@Order,
            customer = mutableStateOf(fetchCustomer()),
            address = mutableStateOf(fetchAddress()),
            carrier = mutableStateOf(fetchCarrier()),
            sale = mutableStateOf(fetchSale()),
            products = mutableStateOf(getProducts()),
            transactions = mutableStateOf(getTransactions())
        )
    }
}

/**
 * -- auto-generated definition
 * create table shop_orders
 * (
 *     id              bigint unsigned auto_increment
 *         primary key,
 *     order_id        varchar(255)                            not null,
 *     sale_id         int                                     null,
 *     carrier_id      int                                     null,
 *     address_id      int unsigned                            null,
 *     customer_id     varchar(255)                            not null,
 *     status          varchar(255) default 'PENDING_APPROVAL' not null,
 *     created_at      timestamp                               null,
 *     updated_at      timestamp                               null,
 *     tracking_number varbinary(60)                           null
 * )
 *     collate = utf8mb4_unicode_ci;
 *
 * create index address_id
 *     on shop_orders (address_id);
 *
 * create index customer_id
 *     on shop_orders (customer_id);
 *
 * create index order_id
 *     on shop_orders (order_id);
 *
 */