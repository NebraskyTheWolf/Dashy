package eu.fluffici.dashy.ui.activities.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
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
            PrivacySettingsScreen(mBus = this.mBus)
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
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PrivacySettingsScreen(
    mBus: EventBus
) {
    val setting1 by remember { mutableStateOf(false) }
    val setting2 by remember { mutableStateOf(false) }
    val setting3 by remember { mutableStateOf(false) }

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

            PrivacySettingItem(
                title = "Enable Location Tracking",
                description = "Allow apps to access your location",
                isEnabled = setting1,
                route = "tracking",
                mBus = mBus
            )

            PrivacySettingItem(
                title = "Allow Data Collection",
                description = "Share data with third-party services",
                isEnabled = setting2,
                route = "data",
                mBus = mBus
            )

            PrivacySettingItem(
                title = "Use Biometric Authentication",
                description = "Enable fingerprint or face recognition",
                isEnabled = setting3,
                route = "biometrics",
                mBus = mBus
            )
        }
    }
}

@Composable
fun PrivacySettingItem(
    title: String,
    route: String,
    description: String,
    isEnabled: Boolean,
    mBus: EventBus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.DarkGray,
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    fontFamily = appFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                    fontFamily = appFontFamily
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = {
                   mBus.post(SettingsSwitchStateUpdate(route = route, value = it))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    uncheckedThumbColor = Color.Gray
                )
            )
        }
    }
}