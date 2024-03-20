package eu.fluffici.dashy.ui.activities.modules.impl.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R

@Composable
fun SupportTicketLayout() {
    Scaffold(
        topBar = { SupportTicketTopBar() },
        content = { padding -> SupportTicketContent(padding) }
    )
}

@Composable
fun SupportTicketTopBar() {
    TopAppBar(
        title = { Text(text = "Support Ticket") },
        actions = {
            DropdownMenu(
                modifier = Modifier.padding(end = 16.dp),
                expanded = false,
                onDismissRequest = {},
            ) {
                DropdownMenuItem(onClick = {}) {
                    Text(text = "Close ticket")
                }
                DropdownMenuItem(onClick = {}) {
                    Text(text = "Reopen")
                }
            }
        }
    )
}

@Composable
fun SupportTicketContent(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatHistory()
            MessageBox()
        }
    }
}

@Composable
fun ChatHistory() {
    Column(
        modifier = Modifier
            .padding(end = 16.dp)
    ) {
        repeat(5) {
            SupportTicketItem(
                modifier = Modifier.padding(vertical = 8.dp),
                name = "John Doe",
                email = "john.doe@example.com",
                lastActivity = "2 hours ago"
            )
        }
    }
}

@Composable
fun MessageBox() {
    Column(
        modifier = Modifier
            .padding(start = 16.dp)
    ) {
        // Messaging bar
        MessagingBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Arrow icon
        IconButton(
            onClick = { /* Send message */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_2_svg),
                contentDescription = null
            )
        }
    }
}

@Composable
fun MessagingBar(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(text = "Type a message...") },
        modifier = modifier,
        singleLine = true
    )
}

@Composable
fun SupportTicketItem(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    lastActivity: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle item click */ }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        ProfilePicture()

        // User information
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(text = name, style = MaterialTheme.typography.body1)
            Text(text = email, style = MaterialTheme.typography.body2)
            Text(text = "Last activity: $lastActivity", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.face_id_error_svg),
        contentDescription = null,
        modifier = modifier
            .size(48.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(24.dp)),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}


@Preview
@Composable
fun SupportLayout() {
    SupportTicketLayout()
}