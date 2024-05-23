package eu.fluffici.dashy.ui.activities.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.SettingsSwitchStateUpdate
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.auth.LockScreen
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PrivacySettings : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContent {
            PrivacySettingsScreen(mBus = this.mBus, context = applicationContext)
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        this.mBus.unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(sticky = true,threadMode = ThreadMode.ASYNC)
    fun onClick(event: CardClickEvent) {
        when (event.viewId) {
            "parent" -> {
                newIntent(Intent(applicationContext, MainActivity::class.java))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(sticky = true,threadMode = ThreadMode.ASYNC)
    fun onClick(event: SettingsSwitchStateUpdate) {
        when (event.route) {
            "sensitivePrivacy" -> {
                if (event.value) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Content protection enabled", Toast.LENGTH_LONG).show()
                    }
                    Storage.setContentProtection(applicationContext, true)
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Content protection disabled", Toast.LENGTH_LONG).show()
                    }
                    Storage.setContentProtection(applicationContext, false)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PrivacySettingsScreen(
    mBus: EventBus,
    context: Context
) {
    var hideSensitiveContent by remember { mutableStateOf(Storage.isContentProtection(context)) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardTitle(text = "Privacy Settings", icon = R.drawable.square_arrow_left_svg, true) {
                mBus.post(CardClickEvent("parent"))
            }

            SecuritySettingItem(
                title = "Sensitive content protection",
                route = "sensitivePrivacy",
                description = "Hide all sensitive content on the home page",
                isEnabled = hideSensitiveContent,
                onToggle = { hideSensitiveContent = it },
                mBus = mBus
            )
        }
    }
}