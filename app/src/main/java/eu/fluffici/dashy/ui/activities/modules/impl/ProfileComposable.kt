package eu.fluffici.dashy.ui.activities.modules.impl

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.dashy.entities.PartialUser
import eu.fluffici.dashy.utils.Storage

@Composable
fun Profile(context: Context) {
    val user = remember { mutableStateOf<PartialUser?>(null) }

    LaunchedEffect(key1 = true) {
        user.value = Storage.getUser(context)
    }

    Column(
        modifier = Modifier
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Profile", // replace with string resource
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(10.dp)
        )

        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    user.value?.username?.let {
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            content = {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    user.value?.email?.let { ListItem(text = "Email", value = it) }
                    user.value?.roles?.let { ListItem(text = "Roles", value = it) }
                }
            }
        )
    }
}

@Composable
fun ListItem(text: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 15.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}