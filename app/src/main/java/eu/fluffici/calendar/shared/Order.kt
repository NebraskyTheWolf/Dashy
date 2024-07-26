package eu.fluffici.calendar.shared

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction
import eu.fluffici.dashy.entities.Voucher
import eu.fluffici.dashy.ui.activities.modules.impl.scanner.isBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bouncycastle.util.encoders.Base64

suspend fun fetchOrder(orderId: String): Order? = withContext(Dispatchers.IO)  {
    val client = OkHttpClient()

    var request = Request.Builder()
    request = if (isBase64(orderId)) {
        request.url("https://api.fluffici.eu/api/order?orderId=${String(Base64.decode(orderId))}")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
    } else {
        request.url("https://api.fluffici.eu/api/order?orderId=${orderId}")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .get()
    }

    val response = client.newCall(request.build()).execute()
    if (response.isSuccessful) {
        val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

        if (data.has("error"))
            return@withContext null

        return@withContext Json.decodeFromString<Order>(data.get("data").asJsonObject.get("order").asJsonObject.toString())
    } else {
        Log.d("OrderManager", "Unable to fetch products from the remote server.")
    }

    return@withContext null
}

suspend fun fetchVoucher(encodedData: String?): Pair<eu.fluffici.dashy.entities.Error?, Voucher?> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order/voucher/info?encodedData=${encodedData}")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()

    val response = client.newCall(request.build()).execute()
    if (response.isSuccessful) {
        val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

        if (data.has("error")) {
            return@withContext Pair(eu.fluffici.dashy.entities.Error(
                data.get("status").asBoolean,
                data.get("error").asString,
                data.get("message").asString,
            ), null)
        }

        return@withContext Pair(null, Json.decodeFromString<Voucher>(data.get("data").asJsonObject.toString()))
    }

    return@withContext Pair(eu.fluffici.dashy.entities.Error(
        false,
        "UNABLE_TO_CONNECT",
        "Unable to contact Fluffici servers.",
    ), null)
}


fun makeRefund(orderId: String?): Pair<String?, String?> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order/payment/refund?orderId=${orderId}")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

        if (data.has("error")) {
            return Pair(data.get("message").asString, null)
        }

        return Pair(null, data.get("message").asString)
    }

    return Pair(null, null)
}

fun makeCancellation(orderId: String?): Pair<String?, String?> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order/cancel?orderId=${orderId}")
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

        if (data.has("error")) {
            return Pair(data.get("message").asString, null)
        }

        return Pair(null, data.get("message").asString)
    } else {
        println(response.body?.string())
    }

    return Pair(null, null)
}

suspend fun makeTypedPayment(orderId: String?, paymentType: String, encoded: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(getUrl(orderId, paymentType, encoded))
        .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful) {
        val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

        if (data.has("error")) {
            return@withContext Pair(data.get("message").asString, null)
        }

        return@withContext Pair(null, data.get("message").asString)
    }

    return@withContext Pair(null, null)
}

private fun getUrl(orderId: String?, paymentType: String, encoded: String): String {
    if (paymentType == "VOUCHER")
        return "https://api.fluffici.eu/api/order/payment?orderId=${orderId}&paymentType=VOUCHER&encodedData=${encoded}"
    return "https://api.fluffici.eu/api/order/payment?orderId=${orderId}&paymentType=CASH"
}

suspend fun getProducts(order: Order?): List<Product> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Product>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order?orderId=${order?.order_id}")
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
                        product.get("product_name").asString,
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

suspend fun getTransactions(order: Order?): List<Transaction> = withContext(Dispatchers.IO) {
    val result = mutableListOf<Transaction>()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.fluffici.eu/api/order?orderId=${order?.order_id}")
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
                        order?.order_id,
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