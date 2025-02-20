package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.Dialog
import eu.fluffici.dashy.utils.newIntent

class VoucherInfoActivity : Module(
    "voucher_info",
    "platform.shop.vouchers.read"
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            Dialog(
                encodedData = intent.getStringExtra("encodedData")!!,
                onSuccessConfirm = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                },
                onFailureConfirm = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                }
            )
        }
    }
}