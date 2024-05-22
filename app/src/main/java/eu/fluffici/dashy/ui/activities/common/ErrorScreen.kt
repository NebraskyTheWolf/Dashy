package eu.fluffici.dashy.ui.activities.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.newIntent

class ErrorScreen : PDAAppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        var title = "Application error"

        if (this.intent.hasExtra("title")) {
            title = this.intent.getStringExtra("title").toString()
        }

        var description = ""

        if (this.intent.hasExtra("description")) {
            description = this.intent.getStringExtra("description").toString()
        }

        setContent {
            ErrorScreen(
                title = title,
                description = description,
                onParentClick = {
                    newIntent(Intent(applicationContext, MainActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun CrashAlertScreen(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedAlertIcon()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                fontFamily = appFontFamily,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.body2.copy(color = Color.Black),
                textAlign = TextAlign.Start,
                color = Color.White,
            )
        }
    }
}

@Composable
fun AnimatedAlertIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        painter = painterResource(id = R.drawable.alert_triangle_svg), // replace with your icon
        contentDescription = "Alert Triangle",
        modifier = Modifier
            .size(80.dp)
            .scale(scale),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ErrorScreen(
    title: String,
    description: String,
    onParentClick: () -> Unit = {}
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp))
    {

        Column {
            DashboardTitle(text = "Go back", icon = R.drawable.square_arrow_left_filled_svg, true) {
                onParentClick()
            }

            MaterialTheme {
                CrashAlertScreen(title, description)
            }
        }
    }
}