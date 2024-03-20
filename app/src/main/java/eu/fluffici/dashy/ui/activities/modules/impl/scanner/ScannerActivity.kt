package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.events.module.OrderScannerEvent
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrderDetailsActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ScannerActivity : AppCompatActivity() {
    private val lock: ArrayList<String> = ArrayList()
    private val mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraScreen(AnalyzerType.BARCODE, eventBus = this.mBus, intent = this.intent)
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
       when (event.type) {
           "VOUCHER" -> {
               if (!this.lock.contains(event.orderId)) {

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