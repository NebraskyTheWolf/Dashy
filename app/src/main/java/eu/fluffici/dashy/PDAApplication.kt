package eu.fluffici.dashy

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.multidex.MultiDexApplication
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.scottyab.rootbeer.RootBeer
import eu.fluffici.calendar.shared.ping
import eu.fluffici.dashy.events.auth.DeviceAuthorization
import eu.fluffici.dashy.events.auth.Unauthorized
import eu.fluffici.dashy.events.common.NetworkPing
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.ui.activities.modules.ModuleManager
import eu.fluffici.dashy.utils.RootCheck
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import eu.fluffici.security.DeviceInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class PDAApplication : MultiDexApplication() {

    private lateinit var rootBeer: RootBeer
    private lateinit var magiskCheck: RootCheck

    private val client = OkHttpClient()

    private val mBus = EventBus.getDefault()

    private var isOffline = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        this.mBus.register(this)
        this.rootBeer = RootBeer(applicationContext)

        this.magiskCheck = RootCheck()
        this.magiskCheck.onStart(applicationContext)

        if (this.rootBeer.isRooted
            || this.rootBeer.isRootedWithBusyBoxCheck
            || this.magiskCheck.isMagiskPresent
            || this.magiskCheck.isAlternateRoot) {

            val i = Intent(this@PDAApplication, ErrorScreen::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("title", "Rooted device.")
            i.putExtra("description", "Tempered device detected.")
            newIntent(i)

            return
        }

        // Simple pinger request to detect when the device is offline.
        this.mBus.post(NetworkPing())

        if (isOffline) {
            val i = Intent(this@PDAApplication, ErrorScreen::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("title", "Connectivity issues detected!")
            i.putExtra("description", "It seems like your phone is offline. Please check your internet settings.")
            newIntent(i)

            return
        }

        if (applicationContext.getDeviceInfo().isPDADevice) {
            this.mBus.postSticky(DeviceAuthorization(applicationContext.getDeviceInfo().GetDeviceId()))
        }

        if (Storage.isAuthentified(applicationContext)) {
            System.setProperty("X-Bearer-token", Storage.getAccessToken(applicationContext))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("dashy", "dashy",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            // Check whether notification permission is enabled.
            if (manager?.getNotificationChannel("dashy")?.importance == NotificationManager.IMPORTANCE_NONE) {
                askForNotificationPermission()
            }
        }
    }

    companion object {
        private val moduleManager: ModuleManager = ModuleManager()

        fun getModuleManager(): ModuleManager {
            return moduleManager
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun askForNotificationPermission() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("You have forcefully denied some of the required permissions. Please open settings, go to permissions and allow them.")
            .setPositiveButton("Settings") { _: DialogInterface, _: Int ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }.setCancelable(false).create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.show()
    }

    override fun onLowMemory() {
        super.onLowMemory()

        this.mBus.unregister(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        this.mBus.unregister(this)

        if (!Storage.isRememberMe(applicationContext))
            Storage.removeAll(applicationContext)
    }

    @Subscribe(sticky = true)
    fun onLoginFailed(event: Unauthorized) {
        val i = Intent(this@PDAApplication, ErrorScreen::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra("title", "Unauthorized.")
        i.putExtra("description", "You do not have access to this application.")
        newIntent(i)

        this.mBus.removeStickyEvent(event);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onNetworkPing(event: NetworkPing) {
        this.isOffline = ping()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    fun onDeviceAuth(event: DeviceAuthorization) {
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/device/authorization?deviceId=${event.deviceId}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val data = Gson().fromJson(response.body?.string(), JsonObject::class.java);
            if (data.get("status").asBoolean) {
                val body = data.get("data").asJsonObject

                Storage.setAccessToken(this@PDAApplication.applicationContext, body.get("token").asString)
                System.setProperty("X-Bearer-token", body.get("token").asString)

                val i = Intent(this@PDAApplication, MainActivity::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                newIntent(i)
            } else {
                if (data.has("message")
                    && !data.get("message").asString.equals("Toto zařízení je neautorizované."))
                {
                    val i = Intent(this@PDAApplication, ErrorScreen::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("title", "Error while authenticating this device")
                    i.putExtra("description", data.get("message").asString)
                    newIntent(i)
                } else {
                    val i = Intent(this@PDAApplication, ErrorScreen::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("title", "Unauthorized device")
                    i.putExtra("description", "Please register the device on the dashboard with this ID: " + applicationContext.getDeviceInfo().GetDeviceId())
                    newIntent(i)
                }
            }
        } else {
            val i = Intent(this@PDAApplication, ErrorScreen::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("title", "Connectivity issues detected!")
            i.putExtra("description", "It seems like your phone is offline. Please check your internet settings.")
            newIntent(i)
        }

        this.mBus.removeStickyEvent(event);
    }
}