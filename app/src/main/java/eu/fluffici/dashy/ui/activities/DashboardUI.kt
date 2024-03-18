package eu.fluffici.dashy.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardClickEvent
import org.greenrobot.eventbus.EventBus
import java.util.Locale

private val appFontFamily = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.googlesans_bold,
            style = FontStyle.Normal
        ))
)

val Gray = Color(0xFF2D2D2D)

@Composable
fun DashboardUI(eventBus: EventBus) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        Column {
            Text(text = stringResource(id = R.string.welcome),
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = appFontFamily,
                modifier = Modifier.padding(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Insert each of the cards here
                items(6) { index ->
                    DashboardCard(index, eventBus)
                }
            }
        }
    }
}

@Composable
fun DashboardCard(index: Int, eventBus: EventBus) {
    val (image, text) = when (index) {
        0 -> Pair(R.drawable.users_svg, R.string.order)
        1 -> Pair(R.drawable.message_chatbot_svg, R.string.customer)
        2 -> Pair(R.drawable.calendar_clock_svg, R.string.products)
        3 -> Pair(R.drawable.clipboard_data_svg, R.string.settings)
        4 -> Pair(R.drawable.qrcode_svg, R.string.reports)
        else -> Pair(R.drawable.calculator_filled_svg, R.string.accounting)
    }

    Card(shape = RoundedCornerShape(12.dp), backgroundColor = Gray,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                eventBus.post(CardClickEvent(getNameFromIndex(index)))
            }
            .height(160.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()) {

            Image(painter = painterResource(id = image), contentDescription = null,
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .padding(top = 18.dp))

            Text(text = stringResource(id = text),
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = appFontFamily,
                modifier = Modifier.padding(top = 20.dp))
        }
    }
}

private fun getNameFromIndex(index: Int): String {
    return when (index) {
        0 -> "users"
        1 -> "tickets"
        2 -> "calendar"
        3 -> "auditlog"
        4 -> "reports"
        else -> "accounting"
    }
}