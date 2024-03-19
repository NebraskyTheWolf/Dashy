package eu.fluffici.dashy.ui.activities.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    items: List<Pair<Int, String>>,
    selectedIndex: Int,
    onItemSelected: (index: Int) -> Unit
) {
    Surface(
        elevation = 8.dp,
        color = MaterialTheme.colors.surface
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 0.dp, // Remove elevation
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, (icon, contentDescription) ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    if (selected) MaterialTheme.colors.onPrimary else Color.Gray
                )
                val alpha by animateFloatAsState(
                    if (selected) 1f else 0.6f,
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = contentDescription,
                            tint = tint.copy(alpha = alpha)
                        )
                    },
                    selected = selected,
                    onClick = { onItemSelected(index) },
                    label = {  },
                    alwaysShowLabel = false
                )
            }
        }
    }
}
