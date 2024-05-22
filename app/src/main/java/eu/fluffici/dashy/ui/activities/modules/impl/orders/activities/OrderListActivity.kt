package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.shared.makeCancellation
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardOrderClickEvent
import eu.fluffici.dashy.events.module.OrderCancellationEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrdersList
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class OrderListActivity : Module(
    "order_list",
    "platform.orders.read",
    false,
    R.drawable.users_group_svg,
    R.string.orders_list
) {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        this.mBus.register(this)

        setContent {
            OrdersList(
                onParentClick = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                },
                mBus = this.mBus,
                onEmpty = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                    runOnUiThread {
                        Toast.makeText(applicationContext, "No orders has been found.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOrderCardClick(event: CardOrderClickEvent) {
        val intent = Intent(applicationContext, OrderDetailsActivity::class.java).apply {
            putExtra("ORDER", event.order)
        }
        this.startActivity(intent)
    }
}