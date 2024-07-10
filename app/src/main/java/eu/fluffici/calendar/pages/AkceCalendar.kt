package eu.fluffici.calendar.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import eu.fluffici.calendar.StatusBarColorUpdateEffect
import eu.fluffici.calendar.clickable
import eu.fluffici.calendar.rememberFirstCompletelyVisibleMonth
import eu.fluffici.calendar.shared.Akce
import eu.fluffici.calendar.shared.akceDateTimeFormatter
import eu.fluffici.calendar.shared.displayText
import eu.fluffici.calendar.shared.generateFlights
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

private val pageBackgroundColor: Color @Composable get() = colorResource(R.color.example_5_page_bg_color)
private val itemBackgroundColor: Color @Composable get() = colorResource(R.color.example_5_item_view_bg_color)
val toolbarColor: Color @Composable get() = colorResource(R.color.colorPrimary)
private val selectedItemColor: Color @Composable get() = colorResource(R.color.example_5_text_grey)
private val inActiveTextColor: Color @Composable get() = colorResource(R.color.example_5_text_grey_light)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AkceCalendar() {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val akceByDate = remember { mutableStateOf(mapOf<LocalDate, List<Akce>>()) }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val daysOfWeek = remember { daysOfWeek() }

    LaunchedEffect(key1 = true) {
        try {
            val result = generateFlights()
            akceByDate.value = result.groupBy { it.time.toLocalDate() }
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Text("An error occurred: $error")
            }
        } ?: run {
            val akceInSelectedDate = remember(akceByDate.value) {
                derivedStateOf {
                    val date = selection?.date
                    akceByDate.value[date] ?: emptyList()
                }
            }
            StatusBarColorUpdateEffect(toolbarColor)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pageBackgroundColor),
            ) {
                val state = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = daysOfWeek.first(),
                    outDateStyle = OutDateStyle.EndOfGrid,
                )
                val coroutineScope = rememberCoroutineScope()
                val visibleMonth = rememberFirstCompletelyVisibleMonth(state)
                LaunchedEffect(visibleMonth) {
                    // Clear selection if we scroll to a new month.
                    selection = null
                }

                // Draw light content on dark background.
                CompositionLocalProvider(LocalContentColor provides darkColors().onSurface) {
                    eu.fluffici.calendar.animations.CalendarTitle(
                        modifier = Modifier
                            .background(toolbarColor)
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        currentMonth = visibleMonth.yearMonth,
                        goToPrevious = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                            }
                        },
                        goToNext = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                            }
                        },
                    )
                    HorizontalCalendar(
                        modifier = Modifier.wrapContentWidth(),
                        state = state,
                        dayContent = { day ->
                            CompositionLocalProvider(LocalRippleTheme provides AkceRippleTheme) {
                                val colors = if (day.position == DayPosition.MonthDate) {
                                    akceByDate.value[day.date].orEmpty().map { colorResource(it.color) }
                                } else {
                                    emptyList()
                                }
                                Day(
                                    day = day,
                                    isSelected = selection == day,
                                    colors = colors,
                                ) { clicked ->
                                    selection = clicked
                                }
                            }
                        },
                        monthHeader = {
                            MonthHeader(
                                modifier = Modifier.padding(vertical = 8.dp),
                                daysOfWeek = daysOfWeek,
                            )
                        },
                    )
                    Divider(color = pageBackgroundColor)
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(items = akceInSelectedDate.value) { flight ->
                            AkceInformation(flight)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    colors: List<Color> = emptyList(),
    onClick: (CalendarDay) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) selectedItemColor else Color.Transparent,
            )
            .padding(1.dp)
            .background(color = itemBackgroundColor)
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
    ) {
        val textColor = when (day.position) {
            DayPosition.MonthDate -> Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 3.dp, end = 4.dp),
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 12.sp,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            for (color in colors) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(color),
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek> = emptyList(),
) {
    Row(modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White,
                text = dayOfWeek.displayText(uppercase = true),
                fontWeight = FontWeight.Light,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LazyItemScope.AkceInformation(akce: Akce) {
    Row(
        modifier = Modifier
            .fillParentMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(
            modifier = Modifier
                .background(color = colorResource(akce.color))
                .fillParentMaxWidth(1 / 7f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = akceDateTimeFormatter.format(akce.time).uppercase(Locale.ENGLISH),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .background(color = itemBackgroundColor)
                .weight(1f)
                .fillMaxHeight(),
        ) {
            AkceInformation(akce.title, isTitle = true)
        }
        Box(
            modifier = Modifier
                .background(color = itemBackgroundColor)
                .weight(1f)
                .fillMaxHeight(),
        ) {
            AkceInformation(akce.description, isTitle = false)
        }
    }
    Divider(color = pageBackgroundColor, thickness = 2.dp)
}

@Composable
private fun AkceInformation(info: Akce.Info, isTitle: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        val resource = if (isTitle) {
            when (info.status) {
                "CANCELLED" -> R.drawable.circle_x_svg
                "FINISHED", "ENDED" -> R.drawable.checks_svg
                "STARTED" -> R.drawable.activity_svg
                "INCOMING" -> R.drawable.clock_filled_svg
                else -> R.drawable.question_mark_svg
            }
        } else {
            R.drawable.receipt_svg
        }
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Image(painter = painterResource(resource), contentDescription = null)
        }
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable {
                },
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = info.key,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = info.value,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}

// The default dark them ripple is too bright so we tone it down.
private object AkceRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(Color.Gray, lightTheme = false)

    @Composable
    override fun rippleAlpha() = RippleTheme.defaultRippleAlpha(Color.Gray, lightTheme = false)
}