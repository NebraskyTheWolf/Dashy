package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.makeCancellation
import eu.fluffici.calendar.shared.makeRefund
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction
import eu.fluffici.dashy.events.module.OrderCancellationEvent
import eu.fluffici.dashy.events.module.OrderPaymentEvent
import eu.fluffici.dashy.events.module.OrderRefundEvent
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrderDetailsLayout
import eu.fluffici.dashy.ui.activities.modules.impl.users.UserItem
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
                (if (this.intent.hasExtra("refundMessage")) { this.intent.getStringExtra("refundMessage") } else {
                    "Unable to determine request state."
                })?.let {
                    OrderDetailsLayout(
                        order = order,
                        context = this.applicationContext,
                        onPaymentClick = {
                            val intent = Intent(this, OrderPayment::class.java).apply {
                                putExtra("ORDER", order)
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
                            this.startActivity(Intent(this.applicationContext, MainActivity::class.java))
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
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(5.dp)) {

                    Column {
                        DashboardTitle(text = "Go back", icon = R.drawable.square_arrow_left_svg, true) {}

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.alert_triangle_filled_svg),
                                    contentDescription = "Payment",
                                    tint = Color.Red
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "This order does not exists in our records.",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = appFontFamily
                                )
                            }
                        }
                    }
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOrderCancellation(event: OrderCancellationEvent) {
        val result = makeCancellation(orderId = event.order.order_id)
        if (result.first != null) {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("ORDER", event.order)
                putExtra("cancelFailed", true)
                putExtra("refundMessage", result.first)
            }
            this.startActivity(intent)
        } else {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("ORDER", event.order)
                putExtra("cancelSuccess", true)
                putExtra("refundMessage", result.second)
            }
            this.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOrderRefund(event: OrderRefundEvent) {
        val result = makeRefund(orderId = event.order.order_id)

        if (result.first == null && result.second == null) {
            val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                putExtra("ORDER", event.order)
                putExtra("refundFailed", true)
                putExtra("refundMessage", "Unable to contact Fluffici servers.")
            }
            this.startActivity(intent)
        } else {
            if (result.first != null) {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("ORDER", event.order)
                    putExtra("refundFailed", true)
                    putExtra("refundMessage", result.first)
                }
                this.startActivity(intent)
            }
            if (result.second != null) {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("ORDER", event.order)
                    putExtra("refundSuccess", true)
                    putExtra("refundMessage", result.second)
                }
                this.startActivity(intent)
            }
        }
    }
}