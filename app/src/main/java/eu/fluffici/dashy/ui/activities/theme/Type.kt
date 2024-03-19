package eu.fluffici.dashy.ui.activities.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import eu.fluffici.dashy.R

val spartanFontFamily = FontFamily(
    Font(R.font.googlesans_bold, weight = FontWeight.Bold),
    Font(R.font.googlesans_bold, weight = FontWeight.ExtraBold),
    Font(R.font.googlesans_bold, weight = FontWeight.Light),
    Font(R.font.googlesans_bold, weight = FontWeight.ExtraLight),
    Font(R.font.googlesans_bold, weight = FontWeight.Medium),
    Font(R.font.googlesans_bold, weight = FontWeight.Normal),
    Font(R.font.googlesans_bold, weight = FontWeight.SemiBold),
    Font(R.font.googlesans_bold, weight = FontWeight.Thin),
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    ),
    h1 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    h4 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = spartanFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )

)