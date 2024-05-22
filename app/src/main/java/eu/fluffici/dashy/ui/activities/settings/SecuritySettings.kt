package eu.fluffici.dashy.ui.activities.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SecuritySettings : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContent {
            SecuritySettingsScreen(
                mBus = this.mBus,
                appLock = Storage.hasAuthentication(applicationContext)
            )
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
            "lock" -> {
                if (event.value) {
                    newIntent(Intent(applicationContext, LockScreen::class.java).apply {
                        putExtra("capture", true)
                    })
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "App lock disabled.", Toast.LENGTH_LONG).show()
                    }
                    Storage.clearAuthentication(applicationContext)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SecuritySettingsScreen(
    mBus: EventBus,
    appLock: Boolean
) {
    var appLockEnabled by remember { mutableStateOf(appLock) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardTitle(text = "Security Settings", icon = R.drawable.square_arrow_left_svg, true) {
                mBus.post(CardClickEvent("parent"))
            }

            SecuritySettingItem(
                title = "Enable App Lock",
                route = "lock",
                description = "Lock the app with a PIN and fingerprint",
                isEnabled = appLockEnabled,
                onToggle = { appLockEnabled = it },
                mBus = mBus
            )
        }
    }
}

@Composable
fun SecuritySettingItem(
    title: String,
    route: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
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
                    style = MaterialTheme.typography.h6.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray)
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    onToggle(it)
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