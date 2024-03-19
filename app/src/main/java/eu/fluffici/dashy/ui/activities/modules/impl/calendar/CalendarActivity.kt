package eu.fluffici.dashy.ui.activities.modules.impl.calendar

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.pages.AkceCalendar
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.Module
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus

class CalendarActivity : Module(
    "calendar",
    "api.calendar.add",
    false,
    R.drawable.calendar_event_svg,
    R.string.products
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
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}