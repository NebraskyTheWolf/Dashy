package eu.fluffici.dashy.ui.activities.modules.impl.logs

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.PaginateCurrentPageEvent
import eu.fluffici.dashy.events.module.PaginateNextPageEvent
import eu.fluffici.dashy.events.module.PaginatePrevPageEvent
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.core.fragments.AuditTableFragment
import eu.fluffici.dashy.ui.core.fragments.TableFragment
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AuditActivity : Module(
    "auditlog",
    "platform.audit.read",
    false
) {

    private val mBus: EventBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()
        setContentView(R.layout.users_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.activity_container, AuditTableFragment(), AuditTableFragment::class.java.getSimpleName()).commit()
        }

        val next = findViewById<ImageButton>(R.id.next_button)
        next.setOnClickListener {
            this.mBus.post(PaginateNextPageEvent())
        }
        val prev = findViewById<ImageButton>(R.id.previous_button)
        prev.setOnClickListener {
            this.mBus.post(PaginatePrevPageEvent())
        }
    }

    @Subscribe
    fun currentPage(event: PaginateCurrentPageEvent) {
        runOnUiThread {
            Toast.makeText(applicationContext, "Current page: ${event.pageId}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
        this.mBus.unregister(this)
    }
}