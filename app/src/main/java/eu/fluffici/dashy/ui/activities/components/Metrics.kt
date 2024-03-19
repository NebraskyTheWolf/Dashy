package eu.fluffici.dashy.ui.activities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun MetricCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AutoSizeText(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AutoSizeText(
                text = value,
                style = MaterialTheme.typography.h6
            )
        }
    }
}



@Composable
fun AutoSizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            Text(
                text = text,
                style = style,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp
            )
        }
    ) { measurables, constraints ->
        val placeable = measurables.firstOrNull()?.measure(constraints)

        val availableWidth = constraints.maxWidth

        if (placeable != null && placeable.width > availableWidth) {
            // Adjust font size
            val adjustedFontSize = calculateAdjustedFontSize(style.fontSize, placeable.width, availableWidth)

            // Re-measure with adjusted font size
            val adjustedTextStyle = style.copy(fontSize = adjustedFontSize)
            val adjustedPlaceable = measurables.firstOrNull()?.measure(constraints.copy(minWidth = 0, maxWidth = Int.MAX_VALUE))

            layout(adjustedPlaceable?.width ?: 0, adjustedPlaceable?.height ?: 0) {
                adjustedPlaceable?.place(0, 0)
            }
        } else {
            layout(placeable?.width ?: 0, placeable?.height ?: 0) {
                placeable?.place(0, 0)
            }
        }
    }
}

private fun calculateAdjustedFontSize(
    originalFontSize: TextUnit,
    originalWidth: Int,
    availableWidth: Int
): TextUnit {
    val scale = availableWidth.toFloat() / originalWidth.toFloat()
    val scaledFontSize = originalFontSize.value * scale
    return (max(scaledFontSize, 1f)).sp
}