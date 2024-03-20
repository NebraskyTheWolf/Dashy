package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrderUI
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Base64

class OrdersActivity : Module(
    "orders",
    "platform.shop.orders.read",
    false,
    R.drawable.qrcode_svg,
    R.string.orders
) {

    private val mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            OrderUI(eventBus = this.mBus, onParentClick = {
                this.newIntent(Intent(applicationContext, MainActivity::class.java))
            })
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()

        this.mBus.unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onCardClick(event: CardClickEvent) {
        when (event.viewId) {
            "scan_order" -> {
                val intent = Intent(this, OrderDetailsActivity::class.java).apply {
                    putExtra("ORDER", fetchOrder(Base64.getEncoder().encodeToString("deeae454-ec6d-4b6a-a7fb-30e2f75ba0b4".toByteArray())).second)
                }

                this.startActivity(intent)
            }
        }
    }
}