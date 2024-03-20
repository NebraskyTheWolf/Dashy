package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.OrderScannerEvent
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderDetailsActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderPostPaymentActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.VoucherInfoActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.regex.Matcher
import java.util.regex.Pattern


class ScannerActivity : AppCompatActivity() {
    private val lock: ArrayList<String> = ArrayList()
    private val mBus = EventBus.getDefault()

    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraScreen(AnalyzerType.BARCODE, eventBus = this.mBus, intent = this.intent)
        }

        if (this.intent.hasExtra("ORDER")) {
            this.order = intent.extras?.getParcelable("ORDER")
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
        this.lock.clear()
    }

    @Synchronized
    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onScannedObject(event: OrderScannerEvent) {
        if (isBase64(event.orderId)) {
            when (event.type) {
                "VOUCHER" -> {
                    if (!this.lock.contains(event.orderId)) {
                        if (this.intent.hasExtra("ORDER")) {
                            val intent = Intent(this, OrderPostPaymentActivity::class.java).apply {
                                putExtra("ORDER", this@ScannerActivity.order)
                                putExtra("paymentType", event.type)
                                putExtra("encodedData", event.orderId)
                            }

                            this.startActivity(intent)
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Sorry but the order was missing :(", Toast.LENGTH_SHORT).show()
                            }
                        }

                        this.lock.add(event.orderId)
                    }
                }
                "VOUCHER_INFO" -> {
                    if (!this.lock.contains(event.orderId)) {
                        val intent = Intent(this, VoucherInfoActivity::class.java).apply {
                            putExtra("encodedData", event.orderId)
                        }

                        this.startActivity(intent)

                        this.lock.add(event.orderId)
                    }
                }
                "ORDER" -> {
                    if (!this.lock.contains(event.orderId)) {
                        val intent = Intent(this, OrderDetailsActivity::class.java).apply {
                            putExtra("ORDER", fetchOrder(event.orderId).second)
                        }

                        this.startActivity(intent)
                        this.lock.add(event.orderId)
                    }
                }
            }
        }
    }


}