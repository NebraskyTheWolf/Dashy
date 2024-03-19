package eu.fluffici.dashy.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.modules.impl.ProfileActivity
import eu.fluffici.dashy.ui.activities.modules.impl.calendar.CalendarActivity
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.OrdersActivity
import eu.fluffici.dashy.ui.activities.modules.impl.support.SupportActivity
import eu.fluffici.dashy.ui.activities.modules.impl.users.UsersActivity
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardUI(context = applicationContext, eventBus = this.mBus)
        }

        this.mBus.register(this)
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onClick(event: CardClickEvent) {
        when (event.viewId) {
            "users" -> {
              newIntent(Intent(applicationContext, UsersActivity::class.java))
            }
            "support" -> {
                newIntent(Intent(applicationContext, SupportActivity::class.java))
            }
            "calendar" -> {
                newIntent(Intent(applicationContext, CalendarActivity::class.java))
            }
            "auditlog" -> {
                newIntent(Intent(applicationContext, AuditActivity::class.java))
            }
            "orders" -> {
                newIntent(Intent(applicationContext, OrdersActivity::class.java))
            }
            "profile" -> {
                newIntent(Intent(applicationContext, ProfileActivity::class.java))
            }
            "parent" -> {
                newIntent(this.intent)
            }
        }

        this.mBus.removeStickyEvent(event);
    }
}