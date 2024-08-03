package eu.fluffici.dashy.ui.activities.modules.impl.product.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.CardOrderClickEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.ProductListUI
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.ProductUI
import eu.fluffici.dashy.ui.activities.modules.impl.scanner.ScannerActivity
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProductListActivity : Module(
    "products_list",
    "platform.shop.products.read",
    false,
    R.drawable.apps_filled_svg,
    R.string.products_one
) {

    private val mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        this.mBus.register(this)

        setContent {
            ProductListUI(
                onParentClick = {
                    this.newIntent(Intent(applicationContext, MainActivity::class.java))
                },
                mBus = this.mBus
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
    }

    @Subscribe
    fun onCardClick(event: CardOrderClickEvent) {
       startActivity(Intent(applicationContext, ProductDetailsActivity::class.java).apply {
           putExtra("productId", event.order)
       })
    }
}