package eu.fluffici.dashy.ui.activities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R

@Composable
fun ModernCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (checked) MaterialTheme.colors.primary else Color.White.copy(
                        alpha = 0.1f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(id = R.drawable.check_svg),
                    contentDescription = "Checked",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}