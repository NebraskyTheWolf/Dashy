package eu.fluffici.dashy.ui.activities.modules.impl.logs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.fluffici.calendar.shared.Audit
import eu.fluffici.calendar.shared.generateAudit
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.Gray
import eu.fluffici.dashy.ui.activities.appFontFamily

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuditLogList(onParentClick: () -> Unit = {}) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val auditLogList = remember { mutableStateOf(listOf<Audit.AuditLogEntry>()) }

    LaunchedEffect(key1 = true) {
        try {
            val result = generateAudit()
            auditLogList.value = result
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

@Composable
fun LoadingIndicator() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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
