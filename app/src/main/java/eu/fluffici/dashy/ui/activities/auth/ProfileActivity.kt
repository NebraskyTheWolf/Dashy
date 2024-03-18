package eu.fluffici.dashy.ui.activities.auth

import android.os.Bundle
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import org.greenrobot.eventbus.EventBus

class ProfileActivity : PDAAppCompatActivity() {
    private var mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
    }
}