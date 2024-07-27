package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import eu.fluffici.dashy.events.module.OrderScannerEvent
import eu.fluffici.dashy.getDeviceInfo
import eu.fluffici.dashy.isUPCAFormat
import eu.fluffici.dashy.ui.activities.common.ScannerInstScreen
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderDetailsActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderPostPaymentActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.VoucherInfoActivity
import eu.fluffici.dashy.ui.activities.modules.impl.product.activities.ProductDetailsActivity
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.PDAScanner
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ScannerActivity : PDAAppCompatActivity() {
    private val lock: ArrayList<String> = ArrayList()
    private val mBus = EventBus.getDefault()
    private var scanner: PDAScanner = PDAScanner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.mBus.register(this)

        if (applicationContext.getDeviceInfo().isPDADevice) {
            setContent {
                ScannerInstScreen(applicationContext)
            }
            this.scanner.startScan(this, this.intent, this.mBus)
        } else {
            setContent {
                CameraScreen(AnalyzerType.BARCODE, eventBus = this.mBus, intent = this.intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
        this.lock.clear()

        this.scanner.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onScannedObject(event: OrderScannerEvent) {
        if (isBase64(event.result)) {
            when (event.type) {
                "VOUCHER" -> {
                    if (this.applicationContext.getDeviceInfo().isPDADevice) {
                        if (this.intent.hasExtra("orderId")) {
                            val intent = Intent(this, OrderPostPaymentActivity::class.java).apply {
                                putExtra("orderId", intent.getStringExtra("orderId"))
                                putExtra("paymentType", event.type)
                                putExtra("encodedData", event.result)
                                putExtra("isPDA", true)
                            }

                            this.startActivity(intent)
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Sorry but the orderId was missing :(", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        if (!this.lock.contains(event.result)) {
                            if (this.intent.hasExtra("orderId")) {
                                val intent = Intent(this, OrderPostPaymentActivity::class.java).apply {
                                    putExtra("orderId", intent.getStringExtra("orderId"))
                                    putExtra("paymentType", event.type)
                                    putExtra("encodedData", event.result)
                                    putExtra("isPDA", true)
                                }

                                this.startActivity(intent)
                            } else {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Sorry but the orderId was missing :(", Toast.LENGTH_SHORT).show()
                                }
                            }

                            this.lock.add(event.result)
                        }
                    }
                }
                "VOUCHER_INFO" -> {
                    if (this.applicationContext.getDeviceInfo().isPDADevice) {
                        val intent = Intent(this, VoucherInfoActivity::class.java).apply {
                            putExtra("encodedData", event.result)
                        }

                        this.startActivity(intent)
                    } else {
                        if (!this.lock.contains(event.result)) {
                            val intent = Intent(this, VoucherInfoActivity::class.java).apply {
                                putExtra("encodedData", event.result)
                            }

                            this.startActivity(intent)

                            this.lock.add(event.result)
                        }
                    }
                }
                "ORDER" -> {
                    if (this.applicationContext.getDeviceInfo().isPDADevice) {
                        val intent = Intent(this, OrderDetailsActivity::class.java).apply {
                            putExtra("orderId", event.result)
                        }

                        this.startActivity(intent)
                    } else {
                        if (!this.lock.contains(event.result)) {
                            val intent = Intent(this, OrderDetailsActivity::class.java).apply {
                                putExtra("orderId", event.result)
                            }

                            this.startActivity(intent)
                            this.lock.add(event.result)
                        }
                    }
                }
                "PRODUCT" -> {
                    runOnUiThread {
                        Toast.makeText(applicationContext, event.result, Toast.LENGTH_SHORT).show()
                    }
                    if (isUPCAFormat(event.result)) {
                        if (this.applicationContext.getDeviceInfo().isPDADevice) {
                            val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                                putExtra("productId", event.result)
                            }

                            this.startActivity(intent)
                        } else {
                            if (!this.lock.contains(event.result)) {
                                val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                                    putExtra("productId", event.result)
                                }

                                this.startActivity(intent)
                                this.lock.add(event.result)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Sorry, but this Barcode is not owned by Fluffici.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}