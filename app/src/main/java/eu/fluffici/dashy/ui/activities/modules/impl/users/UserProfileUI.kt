package eu.fluffici.dashy.ui.activities.modules.impl.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.User
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@Composable
fun UserProfileScreen(
    user: User,
    lastLogins: List<String> = listOf(),
    lastAuditLogs: List<String>  = listOf(),
    onTerminateClicked: () -> Unit = {},
    onParentClick: () -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        isLoading.value = false
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
                    LastAuditLogsCard(lastAuditLogs = lastAuditLogs)
                    Spacer(modifier = Modifier.height(24.dp))

                    TerminateButton(onClick = onTerminateClicked)
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
fun LastAuditLogsCard(lastAuditLogs: List<String>) {
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
            lastAuditLogs.forEach { auditLog ->
                Text(
                    text = "• $auditLog",
                    fontSize = 14.sp,
                    color = Color.Black
                )
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

@Preview
@Composable
fun PreviewUserProfileScreen() {
    UserProfileScreen(
        user = User(
            id = 0,
            name = "OwO",
            email = "owo@fluffici.eu",
            avatar = 0,
            avatarId = null,
            iconBadges = listOf(
                R.drawable.shield_check_filled_svg,
                R.drawable.code_svg,
                R.drawable.file_analytics_svg,
            )
        ),
        lastLogins = listOf("Last Login 1", "Last Login 2"),
        lastAuditLogs = listOf("Audit Log 1", "Audit Log 2")
    ) {}
}

