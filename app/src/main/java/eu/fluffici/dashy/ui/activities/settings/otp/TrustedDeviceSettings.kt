package eu.fluffici.dashy.ui.activities.settings.otp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.tongdun.mobrisk.TDRisk
import com.google.gson.Gson
import com.google.gson.JsonObject
import eu.fluffici.calendar.shared.getTrustedDevices
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.auth.AddTrustedDevice
import eu.fluffici.dashy.events.auth.RemoveTrustedDevice
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.SettingsSwitchStateUpdate
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.newIntent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TrustedDeviceSettings : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContent {
            TrustedDeviceSettingsScreen(
                mBus = mBus,
                onAddDeviceClick = {
                   mBus.post(AddTrustedDevice())
                },
                onDeleteDeviceClick = {
                    mBus.post(RemoveTrustedDevice(it))
                }
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onAddDevice(event: AddTrustedDevice) {
        val body = JsonObject()
        val deviceInfo = TDRisk.getBlackbox()

        body.addProperty("deviceId", deviceInfo.getString("device_id"))
        body.addProperty("modelName", deviceInfo.getJSONObject("device_detail").getString("model"))

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/user/@me/trusted-devices/add")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .post(body.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val result: JsonObject = Gson().fromJson(response.body?.string(), JsonObject::class.java)

            runOnUiThread {
                Toast.makeText(applicationContext, result.get("message").asString, Toast.LENGTH_SHORT).show()
            }

            newIntent(Intent(applicationContext, MainActivity::class.java))
            newIntent(Intent(applicationContext, this::class.java))
        } else {
            runOnUiThread {
                Toast.makeText(applicationContext, "Unable to add your device.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onRemoveDevice(event: RemoveTrustedDevice) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/user/@me/trusted-devices/remove/${event.deviceId}")
            .header("Authorization", "Bearer ${System.getProperty("X-Bearer-token")}")
            .delete()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            runOnUiThread {
                Toast.makeText(applicationContext, "Your device has been delete.", Toast.LENGTH_SHORT).show()
            }

            newIntent(Intent(applicationContext, MainActivity::class.java))
            newIntent(Intent(applicationContext, this::class.java))
        } else {
            runOnUiThread {
                Toast.makeText(applicationContext, "Unable to delete this device.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class TrustedDevice(
    val deviceId: String,
    val modelName: String,
    val registrationId: String,
    val createdAt: Long
)

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TrustedDeviceSettingsScreen(
    mBus: EventBus,
    onAddDeviceClick: () -> Unit,
    onDeleteDeviceClick: (String) -> Unit
) {

    val isLoading = remember { mutableStateOf(true) }
    val trustedDevices = remember { mutableStateOf<List<TrustedDevice>>(emptyList()) }

    LaunchedEffect(trustedDevices) {
        try {
            trustedDevices.value = getTrustedDevices()
        } catch (e: Exception) {
            isLoading.value = true
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LoadingIndicator()
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardTitle(
                    text = "Trusted Devices",
                    icon = R.drawable.square_arrow_left_svg,
                    isOnBeginning = true
                ) {
                    mBus.post(CardClickEvent("parent"))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(trustedDevices.value) { device ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = {
                                if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                                    onDeleteDeviceClick(device.deviceId)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.trash_svg),
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            },
                            dismissContent = {
                                Card(
                                    backgroundColor = Color(0xFF1F1F1F),
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(0)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = device.modelName,
                                            style = MaterialTheme.typography.subtitle1,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onAddDeviceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                ) {
                    Text(text = "Add New Device", color = Color.White)
                }
            }
        }
    }
}
