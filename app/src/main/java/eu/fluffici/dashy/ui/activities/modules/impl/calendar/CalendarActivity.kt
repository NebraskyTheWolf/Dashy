package eu.fluffici.dashy.ui.activities.modules.impl.calendar

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.pages.AkceCalendar
import eu.fluffici.dashy.ui.activities.modules.Module
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus

class CalendarActivity : Module(
    "calendar",
    "api.calendar.add",
    false
) {

    private val mBus: EventBus = EventBus.getDefault()
    private var mClient = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            AkceCalendar()
        }

        if (System.getProperty("X-TOAST-ENABLED") == "true") {
            Toast.makeText(applicationContext, System.getProperty("X-TOAST-MESSAGE"), Toast.LENGTH_LONG).show()
            System.setProperty("X-TOAST-ENABLED", null)
            System.setProperty("X-TOAST-MESSAGE", null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}