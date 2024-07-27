package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.SplashScreen
import eu.fluffici.dashy.ui.activities.common.RequestPermissionScreen
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.OrderUI
import eu.fluffici.dashy.ui.activities.modules.impl.scanner.ScannerActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
            RequestPermissionScreen(
                permissionName = "Camera",
                permission = Manifest.permission.CAMERA,
                onChecking = {
                    SplashScreen(mBus = this.mBus, isCycling = true)
                },
                onGranted = {
                    OrderUI(eventBus = this.mBus, onParentClick = {
                        this.newIntent(Intent(applicationContext, MainActivity::class.java))
                    })
                }
            )
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()

        this.mBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onCardClick(event: CardClickEvent) {
        when (event.viewId) {
            "scan_order" -> {
                val intent = Intent(this, ScannerActivity::class.java).apply {
                    putExtra("isOrder", true)
                }
                this.startActivity(intent)
            }
            "scan_order_long_click" -> {
                Storage.isOrderFocusMode = true
                val intent = Intent(this, ScannerActivity::class.java).apply {
                    putExtra("isOrder", true)
                }
                this.startActivity(intent)

                runOnUiThread {
                    Toast.makeText(applicationContext, "You enabled the Focus mode.", Toast.LENGTH_SHORT).show()
                }
            }
            "voucher_info" -> {
                val intent = Intent(this, ScannerActivity::class.java).apply {
                    putExtra("isVoucherInfo", true)
                }
                this.startActivity(intent)
            }
            "orders_list" -> {
                val intent = Intent(applicationContext, OrderListActivity::class.java)
                this.startActivity(intent)
            }
        }
    }
}