package eu.fluffici.dashy.ui.activities.modules.impl.support

import android.os.Bundle
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.Module
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus

class SupportActivity : Module(
    "support",
    "platform.shop.support.read",
    true,
    R.drawable.message_chatbot_svg,
    R.string.customer
) {

    private val mBus: EventBus = EventBus.getDefault()
    private var mClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()
        //this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}