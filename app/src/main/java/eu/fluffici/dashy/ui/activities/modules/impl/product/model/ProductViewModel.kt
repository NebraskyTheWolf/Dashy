package eu.fluffici.dashy.ui.activities.modules.impl.product.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.guanmai.scanner.IScannerManager
import cn.guanmai.scanner.SupporterManager
import cn.guanmai.scanner.SupporterManager.IScanListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import eu.fluffici.dashy.entities.ProductBody
import eu.fluffici.dashy.getDeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

data class DummyProduct(
    var upcId: String,
    var product: ProductBody,
    var quantity: Int,
    var scanTimes: ArrayList<String>
) {
    fun toJSON(): RequestBody {
        val data = JsonObject()

        data.addProperty("productId", upcId)
        data.addProperty("newQuantity", quantity)

        return data.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }
}

data class DummyModal(
    var title: String,
    var message: String
)

data class DummyToast(
    var slug: String,
    var message: String
)

@SuppressWarnings("All")
class ProductViewModel @Inject constructor(val context: Context) : ViewModel(), IScanListener {
    private val client = OkHttpClient()
    private var mSupporterManager: SupporterManager<IScannerManager>? = null

    private val _scannedProducts = MutableStateFlow<List<DummyProduct>>(emptyList())
    val scannedProducts: StateFlow<List<DummyProduct>> get() = _scannedProducts

    private val _isUnknown = MutableStateFlow(Pair(false, DummyModal("", "")))
    val isUnknown: StateFlow<Pair<Boolean, DummyModal>> get() = _isUnknown

    private val _getErrors = MutableStateFlow(Pair(false, DummyModal("", "")))
    val getErrors: StateFlow<Pair<Boolean, DummyModal>> get() = _getErrors

    private val _getSuccess = MutableStateFlow(Pair(false, DummyToast("", "")))
    val getSuccess: StateFlow<Pair<Boolean, DummyToast>> get() = _getSuccess

    private val _setSaved = MutableStateFlow(AtomicInteger(0))
    val getSaved: StateFlow<AtomicInteger> get() = _setSaved

    init {
        mSupporterManager = SupporterManager<IScannerManager>(context, this)
    }

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("cs"))

    private fun getCurrentTime(): String {
        return dateFormat.format(Date())
    }

    override fun onScannerResultChange(result: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleScanResult(result)
        }
    }

    private fun handleScanResult(result: String) {
        val existingProduct = _scannedProducts.value.find { it.upcId == result }
        if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                quantity = existingProduct.quantity + 1,
                scanTimes = ArrayList(existingProduct.scanTimes).apply { add(getCurrentTime()) }
            )
            _scannedProducts.value = _scannedProducts.value.toMutableList().apply {
                this[indexOf(existingProduct)] = updatedProduct
            }
        } else {
            val request = Request.Builder()
                .url("https://api.fluffici.eu/api/product/upc?id=${result}")
                .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)

                    if (!data.get("status").asBoolean) {
                        _isUnknown.value = Pair(true, DummyModal("Unknown Product", data.get("message").asString))
                    } else {
                        val product: ProductBody = Json.decodeFromString(data.get("data").asJsonObject.toString())
                        _scannedProducts.value += DummyProduct(
                            upcId = result,
                            product = product,
                            quantity = 1,
                            scanTimes = arrayListOf(getCurrentTime())
                        )
                    }
                } else {
                    _isUnknown.value = Pair(true, DummyModal("Unknown Product", "Unable to contact Fluffici's server."))
                }
            } catch (e: Exception) {
                _getErrors.value = Pair(true, DummyModal("Unknown Product", "An error occurred while sending the product request."))
            }
        }
    }

    override fun onScannerServiceConnected() {}

    override fun onScannerServiceDisconnected() {}

    override fun onScannerInitFail() {
        if (context.getDeviceInfo().isPDADevice)
            _getErrors.value = Pair(true, DummyModal("Hardware error", "Unable to init HWScanner"))
        else
            _getErrors.value = Pair(true, DummyModal("Locked feature", "Incompatible device, Please use this feature on the PDA."))
    }

    fun simulate() {
        viewModelScope.launch(Dispatchers.IO) {
            handleScanResult("000000000147")
        }
    }

    fun clearProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _scannedProducts.value = emptyList()
        }
        _getSuccess.value = Pair(true, DummyToast("success", "You cleared all scanned products."))
    }

    fun saveProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            scannedProducts.value.forEach {
               try {
                   val response = client.newCall(Request.Builder()
                       .url("https://api.fluffici.eu/api/product/inventory/update")
                       .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
                       .post(it.toJSON())
                       .build()).execute()
                   if (response.isSuccessful) {
                       val data = Gson().fromJson(response.body?.string(), JsonObject::class.java)
                       if (data.has("status") && data.get("status").asBoolean) {
                           _setSaved.value.incrementAndGet()
                       } else {
                           _getErrors.value = Pair(true, DummyModal("Error", "Unable to update quantity for ${data.get("productId").asInt}"))
                       }
                   } else {
                       _getErrors.value = Pair(true, DummyModal("Error", "Unable to update quantity."))
                   }
               } catch (e: Exception) {
                   e.printStackTrace()
               }
            }

            _getSuccess.value = Pair(true, DummyToast("success", "All products have been updated. (${getSaved.value.get()}/${scannedProducts.value.size})"))
            _scannedProducts.value = emptyList()
            _setSaved.value.set(0)
        }
    }

    fun checkProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val flaggedProducts = mutableListOf<Pair<DummyProduct, String>>()

            _scannedProducts.value.forEach { product ->
                val scanTimes = product.scanTimes.map { dateFormat.parse(it) }
                scanTimes.zipWithNext().forEach { (firstTime, secondTime) ->
                    val timeDifference = (secondTime?.time ?: 0) - (firstTime?.time ?: 0)
                    if (timeDifference < 200) {
                        flaggedProducts.add(Pair(product, "Scanned multiple times within a short period (200ms)"))
                    }
                }
            }

            if (flaggedProducts.isNotEmpty()) {
                val message = flaggedProducts.joinToString(separator = "\n") { (product, reason) ->
                    "Product UPC: ${product.upcId}, Reason: $reason"
                }
                _getErrors.value = Pair(true, DummyModal("Mistake found", "Potential mistakes found:\n$message"))
            } else {
                _getSuccess.value = Pair(true, DummyToast("success", "No mistake has been found on the products quantity."))
            }
        }
    }

    fun clearErrors() {
        _isUnknown.value = Pair(false, DummyModal("", ""))
        _getErrors.value = Pair(false, DummyModal("", ""))
        _getSuccess.value = Pair(false, DummyToast("", ""))
        _setSaved.value.set(0)
    }

    private fun fallback(productId: Int, newQuantity: Int): RequestBody {
        val data = JsonObject();

        data.addProperty("productId", productId);
        data.addProperty("newQuantity", newQuantity);

        return data.toString().toRequestBody();
    }
}
