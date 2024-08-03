package eu.fluffici.dashy.ui.activities.modules

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.PermissionCheckEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus

abstract class Module(
    private val name: String,
    private val permission: String,
    private val isRestricted: Boolean = false,
    private val icon: Int = R.drawable.face_id_error_svg,
    private val string: Int = R.string._000_000,
    ) : PDAAppCompatActivity() {

    private val eventBus: EventBus = EventBus.getDefault()

    protected fun performCheck() {
        this.eventBus.postSticky(PermissionCheckEvent(this.getPermission()))
    }

    fun getName(): String {
        return this.name
    }

    fun getDrawable(): Int {
        return this.icon
    }

    fun getText(): Int {
        return this.string
    }

    protected fun destroy() {

    }

    protected fun getParentUI(): Intent {
        return Intent(this.baseContext, MainActivity::class.java)
    }

    private fun getPermission(): String {
        return this.permission
    }
}