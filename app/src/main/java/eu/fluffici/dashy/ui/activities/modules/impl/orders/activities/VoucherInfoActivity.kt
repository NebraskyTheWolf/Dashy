package eu.fluffici.dashy.ui.activities.modules.impl.orders.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts.VoucherInformationScreen

class VoucherInfoActivity : Module(
    "voucher_info",
    "platform.shop.vouchers.read"
) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            VoucherInformationScreen(
                encodedData = intent.getStringExtra("encodedData"),
                onParentClick = {
                    this.startActivity(this.getParentUI())
                },
                unrecognised = {
                    this.startActivity(this.getParentUI().apply {
                        putExtra("error", "The voucher you have scanned is invalid.")
                    })
                }
            )
        }
    }
}