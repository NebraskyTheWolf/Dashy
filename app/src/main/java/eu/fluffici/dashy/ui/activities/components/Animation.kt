package eu.fluffici.dashy.ui.activities.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun ConfettiAnimation(isRunning: Boolean, onParticleSucceed: () -> Unit = {}) {
    val confettiColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow)

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0 with LinearEasing
                500f at 1000 with LinearEasing
                0f at 2000 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    if (isRunning) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            repeat(100) {
                val x = (0..size.width.toInt()).random().toFloat()
                val y = (0..size.height.toInt()).random().toFloat()
                val color = confettiColors.random()
                drawCircle(color, center = Offset(x + xOffset, y + yOffset), radius = 8f)
                onParticleSucceed()
            }
        }
    }
}