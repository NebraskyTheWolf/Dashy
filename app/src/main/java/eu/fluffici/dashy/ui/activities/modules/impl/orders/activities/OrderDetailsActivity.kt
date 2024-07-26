package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import eu.fluffici.calendar.shared.makeCancellation
import eu.fluffici.calendar.shared.makeRefund
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.OrderCancellationEvent
import eu.fluffici.dashy.events.module.OrderRefundEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        val order = intent.getStringExtra("orderId")

        if (order !== null) {
            setContent {
                (if (this.intent.hasExtra("refundMessage")) { this.intent.getStringExtra("refundMessage") } else {
                    "Unable to determine request state."
                })?.let {
                    OrderDetailsLayout(
                        orderId = order,
                        context = this.applicationContext,
                        onPaymentClick = {
                            val intent = Intent(this, OrderPayment::class.java).apply {
                                putExtra("orderId", order)
                            }
                            this.startActivity(intent)
                        },
                        onCancelClick = {
                            this.mBus.post(OrderCancellationEvent(order))
                        },
                        onRefundClick = {
                            this.mBus.post(OrderRefundEvent(order))
                        },
                        onParentClick = {
                            this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                        },
                        paymentFailed = this.intent.hasExtra("paymentFailed"),

                        refundFailed = this.intent.hasExtra("refundFailed"),
                        refundSuccess = this.intent.hasExtra("refundSuccess"),
                        refundMessage = it,

                        cancelFailed = this.intent.hasExtra("cancelFailed"),
                        cancelSuccess = this.intent.hasExtra("cancelSuccess"),
                    )
                }
            }
        } else {
            setContent {
                ErrorScreen(
                    title = "Application error",
                    description = "This order doesn't exists.",
                    onParentClick = {
                        this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                    }
                )
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
        val result = makeCancellation(orderId = event.order)
        if (result.first != null) {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("orderId", event.order)
                putExtra("cancelFailed", true)
                putExtra("refundMessage", result.first)
            }
            this.startActivity(intent)
        } else {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("orderId", event.order)
                putExtra("cancelSuccess", true)
                putExtra("refundMessage", result.second)
            }
            this.startActivity(intent)
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOrderRefund(event: OrderRefundEvent) {
        val result = makeRefund(orderId = event.order)

        if (result.first == null && result.second == null) {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("orderId", event.order)
                putExtra("refundFailed", true)
                putExtra("refundMessage", "Unable to contact Fluffici's servers.")
            }
            this.startActivity(intent)
        } else {
            if (result.first != null) {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("orderId", event.order)
                    putExtra("refundFailed", true)
                    putExtra("refundMessage", result.first)
                }
                this.startActivity(intent)
            }
            if (result.second != null) {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("orderId", event.order)
                    putExtra("refundSuccess", true)
                    putExtra("refundMessage", result.second)
                }
                this.startActivity(intent)
            }
        }
    }
}