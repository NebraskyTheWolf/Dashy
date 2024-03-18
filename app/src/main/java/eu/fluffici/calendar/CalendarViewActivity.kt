package eu.fluffici.calendar

import android.os.Build
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.pages.AkceCalendar

class CalendarComposeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AkceCalendar()
        }
    }
}