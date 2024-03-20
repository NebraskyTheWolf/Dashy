package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.PostOrderPaymentEvent
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.PaymentMethodSelection
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OrderPayment : Module(
    "order_details",
    "platform.orders.write",
    false,
    R.drawable.receipt_svg,
    R.string.search_order
) {

    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        val order = intent.getParcelableExtra("ORDER", Order::class.java)
        if (order != null) {
            setContent {
                PaymentMethodSelection(order = order, eventBus = this.mBus, onCancelClick = {
                    this.newIntent(this.getParentUI())
                })
            }
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()

        this.mBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOrderPaymentConfirm(event: PostOrderPaymentEvent) {
        when (event.type) {
            "CASH" -> {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Cash selected for ${event.order.first_name}", Toast.LENGTH_SHORT).show()
                }
            }
            "VOUCHER" -> {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Voucher selected for ${event.order.first_name}", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                runOnUiThread {
                    Toast.makeText(applicationContext, "This payment method is invalid.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}