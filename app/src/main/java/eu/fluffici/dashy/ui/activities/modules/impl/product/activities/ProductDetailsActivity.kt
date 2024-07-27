package eu.fluffici.dashy.ui.activities.modules.impl.product.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.ProductDetailsUI
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProductDetailsActivity : Module(
    "products",
    "platform.shop.products.read",
    false,
    R.drawable.shopping_bag_check_svg,
    R.string.products_two
) {

    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            ProductDetailsUI(productId = this.intent.getStringExtra("productId")!!, eventBus = this.mBus, onParentClick = {
                this.newIntent(Intent(applicationContext, MainActivity::class.java))
            })
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
        when (event.viewId) { }
    }
}