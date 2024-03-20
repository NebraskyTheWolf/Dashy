package eu.fluffici.dashy.ui.activities.modules

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PermissionEntity
import eu.fluffici.dashy.events.module.PermissionCheckEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.ErrorView
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class Module(
    private val name: String,
    private val permission: String,
    private val isRestricted: Boolean = false,
    private val icon: Int = R.drawable.face_id_error_svg,
    private val string: Int = R.string._000_000,
    ) : AppCompatActivity() {

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
        System.gc()
        newIntent(this.getParentUI())
    }

    protected fun getParentUI(): Intent {
        return Intent(this.baseContext, MainActivity::class.java)
    }

    private fun getPermission(): String {
        return this.permission
    }
}