package eu.fluffici.dashy.ui.activities.modules.impl.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.Audit
import eu.fluffici.calendar.shared.generateAudit
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.Gray
import eu.fluffici.dashy.ui.activities.common.appFontFamily

@Composable
fun AuditLogList(onParentClick: () -> Unit = {}) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val auditLogList = remember { mutableStateOf(listOf<Audit.AuditLogEntry>()) }
    val currentPage = remember { mutableIntStateOf(1) }

    LaunchedEffect(key1 = currentPage.intValue) {
        try {
            val result = generateAudit(currentPage.intValue)
            auditLogList.value = result
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
                Text(error)
            }
        } ?: run {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(5.dp)) {

                Column {
                    DashboardTitle(text = "Audit logs", icon = R.drawable.square_arrow_left_svg, true) {
                        onParentClick()
                    }

                    PaginateButtons(
                        onNextClick = {
                            currentPage.intValue += 1
                            isLoading.value = true
                        },
                        onPrevClick = {
                            currentPage.intValue -= 1
                            isLoading.value = true
                        },
                        currentPage = currentPage.intValue,
                        maxPages = auditLogList.value[0].maxPages
                    ) {
                        LazyColumn {
                            items(auditLogList.value) { log ->
                                AuditLogItem(log)
                            }
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Row(modifier = Modifier.fillMaxWidth().background(Color.Black), horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator(color = Color.Red)
    }
}

@Composable
fun AuditLogItem(log: Audit.AuditLogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 8.dp,
        backgroundColor = Gray,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "User: ${log.user}",
                style = MaterialTheme.typography.body1,
                fontFamily = appFontFamily,
                color = Color.White
            )
            Text(
                text = "Action: ${log.action}",
                style = MaterialTheme.typography.body2,
                fontFamily = appFontFamily,
                color = Color.White
            )
            Text(
                text = "Timestamp: ${log.timestamp}",
                style = MaterialTheme.typography.caption,
                fontFamily = appFontFamily,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun PaginateButtons(
    onNextClick: () -> Unit = {},
    onPrevClick: () -> Unit = {},
    currentPage: Int = 1,
    maxPages: Int = 1,
    function: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        function()

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
           contentAlignment = Alignment.BottomCenter // Aligning content to bottom end
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentPage > 1) {
                    FloatingActionButton(
                        onClick = onPrevClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_badge_left_filled_svg),
                                contentDescription = "Previous",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Prev",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = appFontFamily

                            )
                        }
                    }
                }

                if (currentPage != maxPages) {
                    FloatingActionButton(
                        onClick = onNextClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_badge_right_filled_svg),
                                contentDescription = "Next",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Next",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = appFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}