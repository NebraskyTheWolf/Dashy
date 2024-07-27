package eu.fluffici.dashy.ui.activities.common

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.unit.sp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.utils.Storage

@Composable
fun ScannerInstScreen(context: Context) {
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
                AnimatedIcon()
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Scanner mode",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                fontFamily = appFontFamily,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (Storage.isOrderFocusMode) {
                Text(
                    text = "Order focus mode enabled, Please scan the order QRCode",
                    style = MaterialTheme.typography.h6.copy(color = Color.Black),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter // Aligning content to bottom end
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FloatingActionButton(
                                onClick = {
                                    Storage.isOrderFocusMode = false
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
                                        contentDescription = "Quit",
                                        tint = Color.Red
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Quit",
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontFamily = appFontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Please scan the DataMatrix to continue",
                    style = MaterialTheme.typography.body2.copy(color = Color.Black),
                    textAlign = TextAlign.Start,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun AnimatedIcon() {
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
        painter = painterResource(id = R.drawable.scan_svg),
        contentDescription = "Alert Triangle",
        modifier = Modifier
            .size(80.dp)
            .scale(scale),
        contentScale = ContentScale.Crop
    )
}