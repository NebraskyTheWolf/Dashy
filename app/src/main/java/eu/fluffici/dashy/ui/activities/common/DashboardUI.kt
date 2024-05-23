package eu.fluffici.dashy.ui.activities.common

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.PDAApplication
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PartialUser
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.components.DottedBackground
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.Storage
import org.greenrobot.eventbus.EventBus

val appFontFamily = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.googlesans_bold,
            style = FontStyle.Normal
        ))
)

val Gray = Color(0xFF2D2D2D)

@Composable
fun DashboardUI(context: Context, eventBus: EventBus) {
    val user = remember { mutableStateOf<PartialUser?>(null) }
    val modules = remember { mutableStateOf<List<Module>>(ArrayList()) }

    LaunchedEffect(key1 = true) {
        user.value = Storage.getUser(context)
        modules.value = PDAApplication.getModuleManager().modules
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        DottedBackground()

        Column {
            DashboardTitle(text = "Welcome ${user.value?.username}", icon = R.drawable.user_circle_svg) {
                eventBus.post(CardClickEvent("profile"))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(getPadding(modules.value.size)),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = modules.value) {
                    DashboardCard(module = DashboardUICard(it.getName(), it.getText(), it.getDrawable()), eventBus = eventBus)
                }
            }
        }
    }
}

data class DashboardUICard(
    val name: String,
    val text: Int,
    val icon: Int,
)

@Composable
fun DashboardCard(module: DashboardUICard, eventBus: EventBus) {
    Card(shape = RoundedCornerShape(12.dp), backgroundColor = Gray,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                eventBus.post(CardClickEvent(module.name))
            }
            .height(160.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(id = module.icon), contentDescription = null,
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .padding(top = 18.dp))

            Text(text = stringResource(id = module.text),
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = appFontFamily,
                modifier = Modifier.padding(top = 20.dp))
        }
    }
}

@Composable
fun DashboardTitle(text: String, icon: Int, isOnBeginning: Boolean = false, onClick: () -> Unit) {
    Row(modifier = Modifier
        .clickable(onClick = onClick)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isOnBeginning) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = appFontFamily,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 2.dp)
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = appFontFamily,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 2.dp)
            )
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            )
        }
    }
}

private fun getPadding(size: Int): Int {
    return when {
        size % 2 == 0 -> 2
        size % 3 == 0 -> 3
        else -> 2
    }
}