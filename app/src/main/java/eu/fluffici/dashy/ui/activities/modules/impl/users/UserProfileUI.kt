package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.Audit
import eu.fluffici.calendar.shared.User
import eu.fluffici.calendar.shared.generateUserAudit
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditLogItem
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileScreen(
    user: User,
    lastLogins: List<String> = listOf(),
    lastAuditLogs: List<String>  = listOf(),
    onParentClick: () -> Unit = {}
) {
    val auditLogList = remember { mutableStateOf(listOf<Audit.AuditLogEntry>()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        try {
            val result = generateUserAudit(user.name!!)
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    DashboardTitle(text = "Users", icon = R.drawable.square_arrow_left_svg, true) {
                        onParentClick()
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileHeader(user = user)

                    Spacer(modifier = Modifier.height(24.dp))
                    LastLoginsCard(lastLogins = lastLogins)
                    Spacer(modifier = Modifier.height(24.dp))
                    LastAuditLogsCard(lastAuditLogs = auditLogList.value)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(user = user)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${user.name}'s profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.email!!,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    user.iconBadges?.forEach { badge ->
                        Icon(
                            painter = painterResource(id = badge),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Avatar(user: User) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Gray)
    ) {
        NetworkImage(user = user,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun LastLoginsCard(lastLogins: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Last Logins",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            lastLogins.forEach { login ->
                Text(
                    text = "• $login",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun LastAuditLogsCard(lastAuditLogs: List<Audit.AuditLogEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Last Audit Logs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(lastAuditLogs) { log ->
                    Text(
                        text = "• ${log.action}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = appFontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• ${log.timestamp}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun TerminateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
    ) {
        Text(
            text = "Terminate",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
