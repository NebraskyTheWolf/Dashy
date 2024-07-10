package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import eu.fluffici.calendar.clickable
import eu.fluffici.calendar.shared.User
import eu.fluffici.calendar.shared.generateUsers
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PartialUser
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.logs.PaginateButtons


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsersList(
    onParentClick: () -> Unit = {},
    onUserClick: (user: User) -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val users = remember { mutableStateOf(listOf<User>()) }
    val currentPage = remember { mutableIntStateOf(1) }

    LaunchedEffect(key1 = currentPage.intValue) {
        try {
            val result = generateUsers(currentPage.intValue)
            users.value = result
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
                    DashboardTitle(text = "Users", icon = R.drawable.square_arrow_left_svg, true) {
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
                        maxPages = users.value[0].maxPages!!
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(users.value) { user ->
                                UserItem(user = user, onUserCardClick = onUserClick)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onUserCardClick: (user: User) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserCardClick(user)
            },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            NetworkImage(user = user,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name!!,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = appFontFamily
                )
                Text(
                    text = user.email!!,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }

            user.iconBadges?.forEach { icon ->
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 8.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
fun NetworkImage(user: User, modifier: Modifier = Modifier) {
    if (user.avatar == 1) {
        AsyncImage(
            model = "https://autumn.fluffici.eu/avatars/${user.avatarId}",
            contentDescription = null,
            modifier = modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = "https://ui-avatars.com/api/?name=${user.name}&background=0D8ABC&color=fff",
            contentDescription = null,
            modifier = modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun NetworkImage(user: PartialUser, modifier: Modifier = Modifier) {
    if (user.avatar == 1) {
        AsyncImage(
            model = "https://autumn.fluffici.eu/avatars/${user.avatarId}",
            contentDescription = null,
            modifier = modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = "https://ui-avatars.com/api/?name=${user.username}&background=0D8ABC&color=fff",
            contentDescription = null,
            modifier = modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
    }
}