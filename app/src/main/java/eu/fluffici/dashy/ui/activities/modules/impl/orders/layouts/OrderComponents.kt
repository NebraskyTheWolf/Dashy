package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.FullOrder
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.getOrderStatus
import eu.fluffici.dashy.ui.activities.modules.impl.product.layouts.getPrice
import kotlinx.coroutines.launch

@Composable
fun OrderDetails(context: Context, order: Order) {
    val coroutineScope = rememberCoroutineScope()
    val fullOrderState = remember { mutableStateOf<FullOrder?>(null) }

    LaunchedEffect(order) {
        coroutineScope.launch {
            fullOrderState.value = order.getAllDetails()
        }
    }

    fullOrderState.value?.let { fullOrder ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Order status: ${getOrderStatus(fullOrder.order.status)}",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    fontFamily = appFontFamily
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEFEF))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Customer Information",
                        fontWeight = FontWeight.Bold,
                        fontFamily = appFontFamily,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Name: ${fullOrder.customer.value.first_name} ${fullOrder.customer.value.last_name}",
                        fontFamily = appFontFamily,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Email: ${fullOrder.customer.value.email}",
                        fontFamily = appFontFamily,
                        fontSize = 14.sp
                    )
                    ClickablePhoneNumber(context = context, title = "Phone:", phoneNumber = fullOrder.customer.value.phone)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEFEF))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Customer Address",
                        fontWeight = FontWeight.Bold,
                        fontFamily = appFontFamily,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextWithLabel(label = "Address:", value = fullOrder.address.value.address_one)
                    TextWithLabel(label = "Co.Address:", value = fullOrder.address.value.address_two)
                    TextWithLabel(label = "City:", value = fullOrder.address.value.city)
                    TextWithLabel(label = "Zip Code:", value = fullOrder.address.value.zip)
                    TextWithLabel(label = "Country:", value = fullOrder.address.value.country)
                }
            }
        }
    } ?: run {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 4.dp,
        ) {
            LoadingIndicator()
        }
    }
}

@Composable
fun TextWithLabel(label: String, value: String) {
    Row {
        Text(
            text = "$label ",
            fontFamily = appFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Text(
            text = value,
            fontFamily = appFontFamily,
            fontSize = 8.sp
        )
    }
}

@Composable
fun ClickablePhoneNumber(context: Context, title: String, phoneNumber: String) {
    Row {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontFamily = appFontFamily
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = phoneNumber,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun TransactionsList(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(25.dp)
                        .height(80.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Awaiting transactions...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = appFontFamily
                )
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = appFontFamily
                )
                LazyColumn {
                    items(transactions) { transaction ->
                        TransactionCard(transaction = transaction)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun TransactionCard(transaction: Transaction) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                TransactionDetail(title = "Transaction ID:", value = transaction.transaction_id?.replace("-", "")?.substring(0, 20))
                Spacer(modifier = Modifier.height(4.dp))
                TransactionDetail(title = "Status:", value = transaction.status)
                Spacer(modifier = Modifier.height(4.dp))
                TransactionDetail(title = "Amount:", value = getPrice(transaction.price!!.toDouble()))
                Spacer(modifier = Modifier.height(4.dp))
                TransactionDetail(title = "Method of Payment:", value = transaction.provider)
            }
        }
    }
}

@Composable
fun TransactionDetail(title: String, value: String?) {
    Row {
        Text(
            text = title,
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp // Slightly larger for titles
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value!!,
            fontFamily = appFontFamily,
            fontSize = 12.sp // Smaller size for values
        )
    }
}

@Composable
fun ProductsList(products: List<Product>) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.alert_triangle_filled_svg),
                    contentDescription = "No Products",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This order has no products.",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = appFontFamily
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Products",
                style = MaterialTheme.typography.h6,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp),
                fontFamily = appFontFamily
            )
            LazyColumn {
                items(products) { product ->
                    ProductCard(product = product)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.product_name,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                fontFamily = appFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "Price:",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.DarkGray,
                    fontFamily = appFontFamily
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getPrice((product.price * product.quantity).toDouble()),
                    style = MaterialTheme.typography.body1,
                    color = Color.Black,
                    fontFamily = appFontFamily
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.DarkGray,
                    fontFamily = appFontFamily
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${product.quantity}",
                    style = MaterialTheme.typography.body1,
                    color = Color.Black,
                    fontFamily = appFontFamily
                )
            }
        }
    }
}