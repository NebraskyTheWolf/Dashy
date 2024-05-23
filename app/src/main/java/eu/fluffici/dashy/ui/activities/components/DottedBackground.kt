package eu.fluffici.dashy.ui.activities.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DottedBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotSize = 5.dp.toPx()
        val dotGap = 10.dp.toPx()

        var x = 0f
        var y = 0f

        while (y < size.height) {
            while (x < size.width) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(x, y),
                        radius = dotSize
                    ),
                    radius = dotSize / 2,
                    center = Offset(x, y)
                )
                x += dotSize + dotGap
            }
            x = 0f
            y += dotSize + dotGap
        }
    }
}