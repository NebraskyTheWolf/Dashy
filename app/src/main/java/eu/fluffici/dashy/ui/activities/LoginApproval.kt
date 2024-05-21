package eu.fluffici.dashy.ui.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R

@Composable
fun LoginApprovalRequestScreen(
    browser: String,
    location: String,
    ipAddress: String,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    var isButtonsVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Browser: $browser",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    fontFamily = appFontFamily
                )
                Text(
                    text = "Location: $location",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    fontFamily = appFontFamily
                )
                Text(
                    text = "IP Address: $ipAddress",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    fontFamily = appFontFamily
                )
            }

            AnimatedVisibility(
                visible = isButtonsVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Approve",
                            fontFamily = appFontFamily)
                    }
                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Reject",
                            fontFamily = appFontFamily)
                    }
                }
            }

            LaunchedEffect(Unit) {
                isButtonsVisible = true
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        DashboardTitle(text = "Login Approval Request", icon = R.drawable.square_arrow_left_filled_svg, true) {

        }

        SettingsScreenTheme {
            LoginApprovalRequestScreen(
                browser = "UwU Firefox",
                location = "OwO Land",
                ipAddress = "127.0.0.1",
                onApprove = {},
                onReject = {}
            )
        }
    }
}