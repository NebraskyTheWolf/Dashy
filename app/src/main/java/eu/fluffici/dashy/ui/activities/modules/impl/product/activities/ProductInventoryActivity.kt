package eu.fluffici.dashy.ui.activities.modules.impl.product.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.InventoryUI
import eu.fluffici.dashy.utils.newIntent

class ProductInventoryActivity : Module(
    "products",
    "platform.shop.products.write",
    false,
    R.drawable.shopping_bag_check_svg,
    R.string.products_two
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            InventoryUI(onParentClick = {
                this.newIntent(Intent(applicationContext, MainActivity::class.java))
            }, executeToast = { slug, value ->
                when (slug) {
                    "inventory_saved" -> Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
                    "success" -> Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}