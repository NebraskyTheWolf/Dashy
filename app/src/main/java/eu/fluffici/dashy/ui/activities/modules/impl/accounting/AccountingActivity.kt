package eu.fluffici.dashy.ui.activities.modules.impl.accounting

import android.os.Bundle
import eu.fluffici.dashy.ui.activities.modules.Module
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus

class AccountingActivity : Module(
    "accounting",
    "platform.accounting",
    true
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
        this.mBus.unregister(this)
    }
}