package eu.fluffici.dashy.ui.activities.experiment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.getPendingRequest
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PartialUser
import eu.fluffici.dashy.events.auth.OTPRequest
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.auth.LockScreen
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.users.NetworkImage
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.newIntent
import kotlinx.serialization.Serializable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale

@Serializable
data class IAuthentication(
    val requestId: String,
    val user: PartialUser,

    val status: String,
    val service: String,
    val date: String,
    val ipAddress: String,
    val location: String,

    val expirationEpoch: Int,

    val createdAt: String,
    val updatedAt: String,

)

class LoginConfirmation : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestId: String? = this.intent.getStringExtra("requestId")

        setContent {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(5.dp)) {

                Column {
                    DashboardTitle(text = "Login Confirmation", icon = R.drawable.lock_svg) {}
                    if (requestId != null) {
                        InvitationScreen(requestId = requestId, mBus = mBus)
                    } else {
                        ErrorScreen(
                            title = "Application error",
                            description = "This login request doesn't exists or is expired.",
                            onParentClick = {
                                newIntent(Intent(applicationContext, MainActivity::class.java))
                            }
                        )
                    }
                }
            }
        }

        this.mBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        this.mBus.unregister(this)
    }

    override fun onStop() {
        super.onStop()
        this.mBus.unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(sticky = true,threadMode = ThreadMode.ASYNC)
    fun onClick(event: OTPRequest) {
        when (event.status) {
            "otp_accepted" -> {
                newIntent(Intent(applicationContext, LockScreen::class.java).apply {
                    putExtra("confirm", true)
                    putExtra("action", "otp_accepted")
                    putExtra("actionId", event.requestId)
                })
            }

            "otp_declined" -> {
                newIntent(Intent(applicationContext, LockScreen::class.java).apply {
                    putExtra("confirm", true)
                    putExtra("action", "otp_declined")
                    putExtra("actionId", event.requestId)
                })
            }
        }

        this.mBus.removeStickyEvent(event);
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvitationScreen(
    requestId: String,
    mBus: EventBus
) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val request = remember { mutableStateOf<IAuthentication?>(null) }

    LaunchedEffect(key1 = true) {
        try {
            val result = getPendingRequest(requestId)
            request.value = result
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(error)
            }
        } ?: run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NetworkImage(user = request.value!!.user, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.plus_svg),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.mfa),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "Hello, @${request.value!!.user.username.toLowerCase(Locale.ENGLISH)}, There is your login request.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(30.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = appFontFamily,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedTextCard(listOf(
                    "Service: ${request.value!!.service}",
                    "Date: ${request.value!!.date}",
                    "IP Address: ${request.value!!.ipAddress}",
                    "Location: ${request.value!!.location}",
                ))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You can accept or decline this request. If you didn't request this login, please decline it ASAP.",
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = appFontFamily,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            mBus.post(OTPRequest(requestId = requestId, status = "otp_accepted"))
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.check_svg),
                                contentDescription = "Accept",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Accept",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = appFontFamily
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            mBus.post(OTPRequest(requestId = requestId, status = "otp_declined"))
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.x_svg),
                                contentDescription = "Decline",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Decline",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = appFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTextCard(textList: List<String>) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.0f at 0 with LinearEasing
                0.5f at 0.5.toInt() with LinearEasing
                0.0f at 1 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Card(
        modifier = Modifier.padding(16.dp),
        border = BorderStroke(
            width = 4.dp,
            color = Color.Red.copy(alpha = 0.6f * (1 - pulseAnimation))
        ),
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            textList.forEach { text ->
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    fontFamily = appFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}