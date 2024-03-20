package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction
import eu.fluffici.dashy.events.module.OrderCancellationEvent
import eu.fluffici.dashy.events.module.OrderPaymentEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrderDetailsLayout
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OrderDetailsActivity : Module(
    "order_details",
    "platform.shop.orders.write",
    false,
    R.drawable.receipt_svg,
    R.string.search_order
) {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        val order = intent.extras?.getParcelable<Order>("ORDER")

        if (order !== null) {
            setContent {
                OrderDetailsLayout(
                    order = order,
                    context = this.applicationContext,
                    onPaymentClick = {
                        val intent = Intent(this, OrderPayment::class.java).apply {
                            putExtra("ORDER", order)
                        }
                        this.newIntent(intent)
                    },
                    onCancelClick = {
                        this.mBus.post(OrderCancellationEvent(order))
                    }
                ) {
                    this.startActivity(Intent(this.applicationContext, MainActivity::class.java))
                }
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
    fun onOrderCancellation(event: OrderCancellationEvent) {

    }
}