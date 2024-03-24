package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.OrderPaymentStatusEvent
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.PaymentInProgressScreen
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.TransactionBody
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.TransactionType
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OrderPostPaymentActivity : Module(
    "order_post_payment",
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

        val order = intent.extras?.getParcelable<Order>("ORDER")
        val paymentType = intent.getStringExtra("paymentType")!!
        val encoded = intent.getStringExtra("encodedData") ?: ""

        if (order != null) {
            setContent {
                PaymentInProgressScreen(
                    transaction = TransactionBody(
                        order = order,
                        transactionType = TransactionType.valueOf(paymentType),
                        voucherBody = encoded
                    ),
                    onSuccessConfirm = {
                        this.mBus.post(OrderPaymentStatusEvent(
                            order = order,
                            status = "SUCCESS"
                        ))
                    },
                    onFailureConfirm = {
                        this.mBus.post(OrderPaymentStatusEvent(
                            order = order,
                            status = "FAILED"
                        ))
                    },
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onPaymentStatus(event: OrderPaymentStatusEvent) {
        when (event.status) {
            "SUCCESS" -> {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("ORDER", event.order)
                }

                this.startActivity(intent)
            }
            "FAILED" -> {
                val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
                    putExtra("ORDER", event.order)
                    putExtra("paymentFailed", true)
                }
                this.startActivity(intent)
            }
        }
    }
}

