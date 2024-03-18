package eu.fluffici.dashy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.modules.impl.ProfileActivity
import eu.fluffici.dashy.ui.activities.modules.impl.accounting.AccountingActivity
import eu.fluffici.dashy.ui.activities.modules.impl.calendar.CalendarActivity
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.OrdersActivity
import eu.fluffici.dashy.ui.activities.modules.impl.support.SupportActivity
import eu.fluffici.dashy.ui.activities.modules.impl.users.UsersActivity
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    override fun onStart() {
        super.onStart()
        this.mBus.register(this)
    }

    override fun onStop() {
        super.onStop()

        this.mBus.unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val welcome = findViewById<TextView>(R.id.welcome);
        welcome.setText("Welcome ${Storage.getUser(this.applicationContext).username}")
        welcome.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.user_circle_svg, 0);

        welcome.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("profile"))
        }

        val users = findViewById<CardView>(R.id.users)
        users.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("users"))
        }

        val tickets = findViewById<CardView>(R.id.tickets)
        tickets.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("tickets"))
        }

        val calendar = findViewById<CardView>(R.id.calendar)
        calendar.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("calendar"))
        }

        val auditlog = findViewById<CardView>(R.id.auditlog)
        auditlog.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("auditlog"))
        }

        val reports = findViewById<CardView>(R.id.reports)
        reports.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("reports"))
        }

        val accounting = findViewById<CardView>(R.id.accounting)
        accounting.setOnClickListener {
            this.mBus.postSticky(CardClickEvent("accounting"))
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onClick(event: CardClickEvent) {
        when (event.viewId) {
            "users" -> {
              newIntent(Intent(applicationContext, UsersActivity::class.java))
            }
            "tickets" -> {
                newIntent(Intent(applicationContext, SupportActivity::class.java))
            }
            "calendar" -> {
                newIntent(Intent(applicationContext, CalendarActivity::class.java))
            }
            "auditlog" -> {
                newIntent(Intent(applicationContext, AuditActivity::class.java))
            }
            "reports" -> {
                newIntent(Intent(applicationContext, OrdersActivity::class.java))
            }
            "accounting" -> {
                newIntent(Intent(applicationContext, AccountingActivity::class.java))
            }
            "profile" -> {
                newIntent(Intent(applicationContext, ProfileActivity::class.java))
            }
        }

        this.mBus.removeStickyEvent(event);
    }
}