package eu.fluffici.dashy.ui.activities.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.fetchAccountingStats
import eu.fluffici.calendar.shared.fetchLatestOrder
import eu.fluffici.calendar.shared.toJavaDate
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.FullOrder
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.CardOrderClickEvent
import eu.fluffici.dashy.model.AccountingModel
import eu.fluffici.dashy.ui.activities.components.StyledCard
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.getOrderStatus
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.getPrice
import eu.fluffici.dashy.ui.activities.settings.SettingsScreenTheme
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicInteger

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomePage(
    context: Context,
    mBus: EventBus
) {

    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val accounting = remember { mutableStateOf(listOf<AccountingModel>()) }
    val latestOrder = remember { mutableStateOf<FullOrder?>(null) }

    LaunchedEffect(key1 = true) {
        try {
            val result = fetchAccountingStats()
            accounting.value = result

            val order = fetchLatestOrder()
            order?.let {
                latestOrder.value = order.getAllDetails()
            } ?: run {
                isLoading.value = true
            }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black), contentAlignment = Alignment.Center
            ) {
                Text(error)
            }
        } ?: run {

            val accountingState = accounting.value[0]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Home",
                    color = Color.White,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = appFontFamily
                )
                SettingsScreenTheme {
                    Scaffold {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .verticalScroll(scrollState)
                        ) {
                            latestOrder.value?.let { order ->
                                Spacer(modifier = Modifier.height(16.dp))

                                StyledCard(
                                    context = context,
                                    title = "Latest Order".uppercase(),
                                    borderColor = Color.Red,
                                    onClick = {
                                        mBus.post(CardClickEvent("viewOrder_".plus(order.order.order_id)))
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        val atomic = AtomicInteger(0)
                                        order.products.value.forEach { atomic.addAndGet(it.price * it.quantity) }

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Status:",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = getOrderStatus(order.order.status),
                                                style = MaterialTheme.typography.body2,
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(2f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Customer:",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${order.customer.value.first_name} ${order.customer.value.last_name}",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(2f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Order Date:",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = toJavaDate(order.order.created_at),
                                                style = MaterialTheme.typography.body2,
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(2f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Total:",
                                                style = MaterialTheme.typography.body2,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = getPrice(atomic.get().toDouble()),
                                                style = MaterialTheme.typography.body2,
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(2f)
                                            )
                                        }
                                    }
                                }
                            } ?: run {
                                Spacer(modifier = Modifier.height(16.dp))

                                StyledCard(
                                    context = context,
                                    title = "Latest Order".uppercase(),
                                    borderColor = Color.Red,
                                    onClick = { }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No available order!",
                                            style = MaterialTheme.typography.body1,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            StyledCard(
                                context = context,
                                title = "Accounting statistics".uppercase(),
                                borderColor = Color.Red,
                                onClick = { }) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    StatisticsText(
                                        label = "Balance",
                                        value0 = accountingState.outstandingBalance
                                    )
                                    StatisticsText(
                                        label = "Expenses",
                                        value0 = accountingState.monthlySpending
                                    )
                                    StatisticsText(
                                        label = "Net Profit",
                                        value0 = accountingState.monthlyIncome
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsText(label: String, value0: String) {
    val value: Int = value0.replace(",", "").replace(" Kč", "").toInt()

    val (color) = if (value >= 0) {
        Color.Green to null
    } else {
        Color.Red to painterResource(id = R.drawable.info_triangle_svg)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = appFontFamily
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = getPrice(value.toDouble()).replace(" Kč", ""),
            style = MaterialTheme.typography.body2.copy(
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = appFontFamily
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = " Kč",
            style = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun Settings(mBus: EventBus) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            color = Color.White,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = appFontFamily
        )
        SettingsScreenTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                eu.fluffici.dashy.ui.activities.settings.SettingsScreen(mBus)
            }
        }
    }
}
