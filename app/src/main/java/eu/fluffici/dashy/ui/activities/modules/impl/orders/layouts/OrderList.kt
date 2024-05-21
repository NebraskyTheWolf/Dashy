package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.clickable
import eu.fluffici.calendar.shared.generateOrders
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.logs.PaginateButtons

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersList(
    onParentClick: () -> Unit = {},
    onEmpty: () -> Unit = {},
    onUserClick: (order: Order) -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val orders = remember { mutableStateOf(listOf<Order>()) }
    val currentPage = remember { mutableIntStateOf(1) }


    LaunchedEffect(key1 = currentPage.intValue) {
        try {
            val result = generateOrders(currentPage.intValue)
            orders.value = result

            if (orders.value.isEmpty()) {
                onEmpty()

                errorMessage.value = "Empty"
            }
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
                    DashboardTitle(text = "Orders", icon = R.drawable.square_arrow_left_svg, true) {
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
                        maxPages = orders.value[0].maxPages!!
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(orders.value) { order ->
                                OrderItem(order = order, onUserCardClick = onUserClick)
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
fun OrderItem(
    order: Order,
    onUserCardClick: (order: Order) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserCardClick(order)
            },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = order.first_name!! + " " +  order.last_name!!,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = appFontFamily
                )
                Text(
                    text = order.status!!,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}