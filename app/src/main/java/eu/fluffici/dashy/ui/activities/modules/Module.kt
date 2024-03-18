package eu.fluffici.dashy.ui.activities.modules

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
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
    private val isRestricted: Boolean = false
) : AppCompatActivity() {

    private val tempBus: EventBus = EventBus.getDefault()
    private var mClient = OkHttpClient()
    protected var isAccessible: Boolean = false

     protected fun performCheck() {
         this.tempBus.register(this)
         this.tempBus.postSticky(PermissionCheckEvent(this.getPermission()))
     }

    fun getName(): String {
        return this.name
    }

    protected fun destroy() {
        System.gc()
        this.tempBus.unregister(this)
        newIntent(this.getParentUI())
    }

    private fun getParentUI(): Intent {
        return Intent(this.baseContext, MainActivity::class.java)
    }

    private fun getPermission(): String {
        return this.permission
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onPermissiveCheck(event: PermissionCheckEvent) {
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/user/@me/permission-check")
            .addHeader("Authorization", "Bearer ${Storage.getAccessToken(applicationContext)}")
            .post(event.toJSON())
            .build()
        val response = this.mClient.newCall(request).execute()
        val body = Gson().fromJson(response.body?.string(), PermissionEntity::class.java)
        if (response.isSuccessful) {
            if (body.error !== null) {
                if (body.error === "ACCOUNT_TERMINATED") {
                    val i = Intent(this@Module, ErrorView::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("title", "Uh-Oh")
                    i.putExtra("message", "Your account has been terminated.")
                    return newIntent(i)
                }
            }



            this.isAccessible = body.isGranted
            if (!body.isGranted) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Permission denied.", Toast.LENGTH_LONG).show()
                }
                newIntent(this.getParentUI())
            } else {
                if (this.isRestricted) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "W.I.P", Toast.LENGTH_LONG).show()
                    }
                    newIntent(this.getParentUI())
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(applicationContext, "Unable to check permissions.", Toast.LENGTH_LONG).show()
            }
            newIntent(this.getParentUI())
        }
    }
}