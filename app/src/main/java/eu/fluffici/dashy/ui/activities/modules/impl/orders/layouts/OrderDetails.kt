package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.calendar.shared.getProducts
import eu.fluffici.calendar.shared.getTransactions
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.*
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@Composable
fun OrderDetailsLayout(
    orderId: String,
    context: Context,
    onPaymentClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onParentClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    paymentFailed: Boolean = false,
    refundFailed: Boolean = false,
    refundSuccess: Boolean = false,
    cancelFailed: Boolean = false,
    cancelSuccess: Boolean = false,
    refundMessage: String = ""
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val orders = remember { mutableStateOf<Order?>(null) }
    val products = remember { mutableStateOf(listOf<Product>()) }
    val transactions = remember { mutableStateOf(listOf<Transaction>()) }

    LaunchedEffect(key1 = true) {
        try {
            orders.value = fetchOrder(orderId = orderId)
            if (orders.value == null)
                errorMessage.value = "This order does not exists."
            products.value = getProducts(order = orders.value)
            transactions.value = getTransactions(order = orders.value)
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            ErrorScreen(
                title = "Application error",
                description = error,
                onParentClick = {
                    onParentClick()
                }
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(5.dp)
            ) {

                Column {
                    DashboardTitle(
                        text = "Order from ${orders.value?.first_name}",
                        icon = R.drawable.square_arrow_left_filled_svg,
                        isOnBeginning = true
                    ) {
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
                                Text(text = "Order Details", fontFamily = appFontFamily)
                            }
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 }
                            ) {
                                Text(text = "Transactions", fontFamily = appFontFamily)
                            }
                            Tab(
                                selected = selectedTabIndex == 2,
                                onClick = { selectedTabIndex = 2 }
                            ) {
                                Text(text = "Products", fontFamily = appFontFamily)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        when (selectedTabIndex) {
                            0 -> {
                                OrderDetails(context = context, order = orders.value)

                                if (hasDisputed(transactions.value)) {
                                    DisputeAlertCard(
                                        title = "Dispute Alert",
                                        description = "One or more payment(s) has been disputed by the third party (customer)."
                                    )
                                }

                                if (paymentFailed) {
                                    DisputeAlertCard(
                                        title = "Last payment failed.",
                                        description = "One or more payment(s) has failed."
                                    )
                                }

                                if (refundFailed) {
                                    DisputeAlertCard(
                                        title = "Refund failed.",
                                        description = refundMessage
                                    )
                                }

                                if (refundSuccess) {
                                    DisputeAlertCard(
                                        title = "Successfully refunded.",
                                        description = refundMessage
                                    )
                                }

                                if (cancelFailed) {
                                    DisputeAlertCard(
                                        title = "Cancellation failed.",
                                        description = refundMessage
                                    )
                                }

                                if (cancelSuccess) {
                                    DisputeAlertCard(
                                        title = "Successfully cancelled.",
                                        description = refundMessage
                                    )
                                }

                                ActionButton(
                                    onPaymentClick,
                                    onCancelClick,
                                    onRefundClick,
                                    transaction = transactions.value
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
    onPaymentClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
    transaction: List<Transaction>
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd // Aligning content to bottom end
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hasPaid(transaction)) {
                    if (!hasRefund(transaction)) {
                        FloatingActionButton(
                            onClick = onRefundClick,
                            modifier = Modifier
                                .padding(8.dp)
                                .width(if (screenWidth < 400.dp) 90.dp else 110.dp)
                                .height(if (screenWidth < 400.dp) 35.dp else 40.dp),
                            backgroundColor = Color.White,
                            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 10))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.receipt_refund_svg),
                                    contentDescription = "Refund",
                                    tint = Color.Red
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Refund",
                                    color = Color.Black,
                                    fontSize = if (screenWidth < 400.dp) 12.sp else 14.sp,
                                    fontFamily = appFontFamily

                                )
                            }
                        }
                    } else {
                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier
                                .padding(8.dp)
                                .width(if (screenWidth < 400.dp) 90.dp else 110.dp)
                                .height(if (screenWidth < 400.dp) 35.dp else 40.dp),
                            backgroundColor = Color.Transparent,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock_svg),
                                    contentDescription = "Off",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                } else {
                    FloatingActionButton(
                        onClick = onPaymentClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(if (screenWidth < 400.dp) 90.dp else 110.dp)
                            .height(if (screenWidth < 400.dp) 35.dp else 40.dp),
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
                                fontSize = if (screenWidth < 400.dp) 12.sp else 14.sp,
                                fontFamily = appFontFamily
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(if (screenWidth < 400.dp) 90.dp else 110.dp)
                            .height(if (screenWidth < 400.dp) 35.dp else 40.dp),
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
                                fontSize = if (screenWidth < 400.dp) 10.sp else 11.sp,
                                fontFamily = appFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisputeAlertCard(
    title: String,
    description: String
) {
    val borderPaint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = Color.Red
            strokeWidth = 1f
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ),
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.alert_triangle_svg),
                        contentDescription = "Cancel Order",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = appFontFamily
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = Color.DarkGray,
                    fontFamily = appFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "For more information, please look inside the dashboard.",
                    color = Color.DarkGray,
                    fontFamily = appFontFamily
                )
            }
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                },
                color = Color.Transparent,
                style = Stroke(width = 2.dp.toPx(), pathEffect = borderPaint.pathEffect)
            )
        }
    }
}