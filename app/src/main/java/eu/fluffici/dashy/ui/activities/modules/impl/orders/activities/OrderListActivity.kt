package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrdersList
import eu.fluffici.dashy.utils.newIntent


class OrderListActivity : Module(
    "order_list",
    "platform.orders.read",
    false,
    R.drawable.users_group_svg,
    R.string.orders_list
) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            OrdersList(
                onParentClick = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                },
                onUserClick = {
                    this.newIntent(Intent(this.applicationContext, OrderDetailsActivity::class.java).apply {
                        putExtra("ORDER", it)
                    })
                },
                onEmpty = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                    runOnUiThread {
                        Toast.makeText(applicationContext, "No orders has been found.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}