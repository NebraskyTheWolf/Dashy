package eu.fluffici.dashy.ui.activities.modules.impl.orders

import android.os.Bundle
import androidx.activity.compose.setContent
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.Module

class OrdersActivity : Module(
    "orders",
    "platform.shop.orders.read",
    false,
    R.drawable.qrcode_svg,
    R.string.orders
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            OrderUI(eventBus = this.eventBus)
        }
    }
}