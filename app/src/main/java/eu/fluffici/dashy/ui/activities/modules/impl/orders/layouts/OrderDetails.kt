package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.getProducts
import eu.fluffici.calendar.shared.getTransactions
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction
import eu.fluffici.dashy.ui.activities.DashboardTitle
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsLayout(
    order: Order,
    context: Context,
    onPaymentClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onParentClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val products = remember { mutableStateOf(listOf<Product>()) }
    val transactions = remember { mutableStateOf(listOf<Transaction>()) }

    LaunchedEffect(key1 = true) {
        try {
            products.value = getProducts(order = order)
            transactions.value = getTransactions(order = order)
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
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
                    DashboardTitle(text = "Order from ${order.first_name}", icon = R.drawable.square_arrow_left_filled_svg, true) {
                        onParentClick()
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            backgroundColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    color = Color.White,
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 }
                            ) {
                                Text(text = "Order Details")
                            }
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 }
                            ) {
                                Text(text = "Transactions")
                            }
                            Tab(
                                selected = selectedTabIndex == 2,
                                onClick = { selectedTabIndex = 2 }
                            ) {
                                Text(text = "Products")
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        when (selectedTabIndex) {
                            0 -> {
                                OrderDetails(context = context, order = order)
                                ActionButton(
                                    isPaid = transactions.value.isNotEmpty(),
                                    onPaymentClick,
                                    onCancelClick
                                )
                            }
                            1 -> {
                                TransactionsList(transactions = transactions.value)
                            }
                            2 -> {
                                ProductsList(products = products.value)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ActionButton(
    isPaid: Boolean = false,
    onPaymentClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd // Aligning content to bottom end
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPaid) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.checks_svg),
                            contentDescription = "The order has been already paid.",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "The order has been already paid.",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    FloatingActionButton(
                        onClick = onPaymentClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.receipt_svg),
                                contentDescription = "Payment",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Payment",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(110.dp)
                            .height(40.dp),
                        backgroundColor = Color.White,
                        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.receipt_off_svg),
                                contentDescription = "Cancel Order",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cancel Order",
                                color = Color.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}