package eu.fluffici.dashy.ui.activities.modules.impl.otp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.OtpUI
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.ProductUI
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OTPActivity : Module(
    "otp",
    "platform.firebase.notification.ack",
    false,
    R.drawable.mfa,
    R.string.otp_request
) {

    private val mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            OtpUI(eventBus = this.mBus, onParentClick = {
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onCardClick(event: CardClickEvent) {
        when (event.viewId) {

        }
    }
}