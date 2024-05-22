package eu.fluffici.dashy.ui.activities.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import org.greenrobot.eventbus.EventBus

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(mBus: EventBus) {
    Scaffold() {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .verticalScroll(scrollState)
        ) {
            SettingsCategory(title = "Account")
            SettingsItem(title = "Privacy", description = "Manage your privacy settings", route = "privacy", mBus = mBus)
            SettingsItem(title = "Security", description = "Configure security options", route = "security", mBus = mBus)

            /** TO BE IMPLEMENTED
             *     Spacer(modifier = Modifier.height(16.dp))
             *     SettingsCategory(title = "Notifications")
             *     SettingsItem(title = "Push Notifications", description = "Manage push notifications", route = "push_notifications", mBus = mBus)
             *
             *     Spacer(modifier = Modifier.height(16.dp))
             *     SettingsCategory(title = "Dashboard")
             *     SettingsItem(title = "Layout Arrangement", description = "Edit the dashboard layout with your preferences.", route = "dashboard_layout", mBus = mBus)
             *
             */

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategory(title = "About")
            AppInfo(title = "Version", info = "1.0.0")
            AppInfo(title = "Developer", info = "Vakea")
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp),
        fontFamily = appFontFamily
    )
}

@Composable
fun SettingsItem(title: String, route: String, description: String, mBus: EventBus) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                mBus.post(CardClickEvent(route))
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = appFontFamily)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = Color.Gray, fontSize = 14.sp, fontFamily = appFontFamily)
        }
    }
}

@Composable
fun AppInfo(title: String, info: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = "$title:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = appFontFamily)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = info, color = Color.Gray, fontSize = 12.sp, fontFamily = appFontFamily)
    }
}

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Color.Cyan,
    primaryVariant = Color.Cyan,
    secondary = Color.Cyan,
    background = Color.Black,
    surface = Color.DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@SuppressLint("ConflictingOnColor")
@Composable
fun SettingsScreenTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        darkColors(
            primary = Color.Red ,
            primaryVariant = Color.Red,
            secondary = Color.Red,
            background = Color.Black,
            surface = Color.DarkGray,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}