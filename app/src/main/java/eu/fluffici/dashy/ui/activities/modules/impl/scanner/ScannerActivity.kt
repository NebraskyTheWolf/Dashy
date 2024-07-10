package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.guanmai.scanner.SupporterManager
import cn.guanmai.scanner.SupporterManager.IScanListener
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.OrderScannerEvent
import eu.fluffici.dashy.getDeviceInfo
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.ScannerInstScreen
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderDetailsActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderPostPaymentActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.VoucherInfoActivity
import eu.fluffici.dashy.utils.PDAScanner
import eu.fluffici.dashy.utils.PDAScanner.GMCallback
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ScannerActivity : AppCompatActivity(), GMCallback {
    private val lock: ArrayList<String> = ArrayList()
    private val mBus = EventBus.getDefault()

    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (applicationContext.getDeviceInfo().isPDADevice) {
            setContent {
                ScannerInstScreen()
            }
            PDAScanner.startScan(applicationContext, this.intent, this.mBus, this)
        } else {
            setContent {
                CameraScreen(AnalyzerType.BARCODE, eventBus = this.mBus, intent = this.intent)
            }
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

    override fun onSuccess(intent: Intent, mBus: EventBus, data: String) {
        if (intent.hasExtra("isVoucher"))
            mBus.post(OrderScannerEvent(data, "VOUCHER"))
        if (intent.hasExtra("isVoucherInfo"))
            mBus.post(OrderScannerEvent(data, "VOUCHER_INFO"))
        if (intent.hasExtra("isOrder"))
            mBus.post(OrderScannerEvent(data, "ORDER"))
    }

    override fun onError() {
        runOnUiThread {
            Toast.makeText(applicationContext, "A malfunction has been detected on the PDA device.", Toast.LENGTH_LONG).show()
            newIntent(Intent(applicationContext, MainActivity::class.java))
        }
    }
}