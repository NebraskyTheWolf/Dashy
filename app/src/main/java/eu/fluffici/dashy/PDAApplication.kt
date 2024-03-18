package eu.fluffici.dashy

import android.content.Intent
import androidx.multidex.MultiDexApplication
import com.scottyab.rootbeer.RootBeer
import eu.fluffici.dashy.events.auth.Unauthorized
import eu.fluffici.dashy.ui.activities.common.ErrorView
import eu.fluffici.dashy.utils.RootCheck
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.Executor




open class PDAApplication : MultiDexApplication() {

    private lateinit var rootBeer: RootBeer
    private lateinit var magiskCheck: RootCheck

    private val client = OkHttpClient()

    private val mBus = EventBus.getDefault()

    override fun onCreate() {
        super.onCreate()

        this.rootBeer = RootBeer(applicationContext)

        this.magiskCheck = RootCheck()
        this.magiskCheck.onStart(applicationContext)

        if (this.rootBeer.isRooted
            || this.rootBeer.isRootedWithBusyBoxCheck
            || this.magiskCheck.isMagiskPresent
            || this.magiskCheck.isAlternateRoot) {

            val i = Intent(this@PDAApplication, ErrorView::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("title", "Rooted device.")
            i.putExtra("message", "Tempered device detected.")
            newIntent(i)

            return // Stuck the app loading
        }

        if (Storage.isAuthentified(applicationContext)) {
            System.setProperty("X-Bearer-token", Storage.getAccessToken(applicationContext))
        }

        this.mBus.register(this)
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
        val i = Intent(this@PDAApplication, ErrorView::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra("isUnauthorized", true)
        newIntent(i)

        this.mBus.removeStickyEvent(event);
    }
}