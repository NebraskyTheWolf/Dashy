package eu.fluffici.dashy.ui.activities.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.common.FlufficiDark
import eu.fluffici.dashy.utils.Storage

@Composable
fun StyledCard(
    title: String,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    context: Context,
    content: @Composable () -> Unit,
) {
    var isBlurred by remember { mutableStateOf(Storage.isContentProtection(context)) }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            IconButton(onClick = { isBlurred = !isBlurred }) {
                Image(
                    painter = painterResource(id = if (isBlurred) R.drawable.eye_svg else R.drawable.eye_closed_svg) ,
                    contentDescription = if (isBlurred) "Show content" else "Blur content"
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
                .background(FlufficiDark, shape = RoundedCornerShape(2.dp))

        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .then(if (isBlurred) Modifier.blur(16.dp) else Modifier)
            ) {
                content()
            }
        }
    }
}