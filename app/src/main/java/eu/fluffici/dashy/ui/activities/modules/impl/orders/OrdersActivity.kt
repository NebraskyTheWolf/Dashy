package eu.fluffici.dashy.ui.activities.modules.impl.orders

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.newIntent
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus

class OrdersActivity : Module(
    "orders",
    "platform.shop.orders.read",
    false
) {

    private val mBus: EventBus = EventBus.getDefault()
    private var mClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContentView(R.layout.order_activity)

        val text = findViewById<TextView>(R.id.orderManagement)
        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.square_arrow_left_svg, 0, 0, 0);
        text.setOnClickListener {
            newIntent(Intent(applicationContext, MainActivity::class.java))
        }

        //this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
        this.mBus.unregister(this)
    }
}