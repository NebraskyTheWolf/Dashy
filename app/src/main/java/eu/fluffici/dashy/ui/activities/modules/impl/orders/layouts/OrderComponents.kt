package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.entities.Product
import eu.fluffici.dashy.entities.Transaction

@Composable
fun OrderDetails(context: Context, order: Order) {
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
                text = "Order status: ${order.status}",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Customer Information", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Name: ${order.first_name} ${order.last_name}")
                    Text(text = "Email: ${order.email}")
                    ClickablePhoneNumber(context = context, title = "Phone:", phoneNumber = "${order.phone_number}")
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Customer Address", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Address: ${order.first_address}")
                    Text(text = "Complementary address: ${order.second_address}")
                    Text(text = "Zip code: ${order.postal_code}")
                    Text(text = "Country: ${order.country}")
                }
            }
        }
    }
}

@Composable
fun ClickablePhoneNumber(context: Context, title: String, phoneNumber: String) {
    Row {
        Text(
            text = title,
            color = LocalContentColor.current.copy(alpha = 0.5f)
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
                    fontSize = 18.sp
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
                    modifier = Modifier.padding(bottom = 8.dp)
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Transaction ID: ${transaction.transaction_id}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Status: ${transaction.status}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Amount: ${transaction.price} Kč")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Method of Payment: ${transaction.provider}")
            }
        }
    }
}

@Composable
fun ProductsList(products: List<Product>) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.alert_triangle_filled_svg),
                    contentDescription = "Payment",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "This order has no products.",
                    color = Color.White,
                    fontSize = 18.sp
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
                    text = "Products",
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
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
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 8.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Product Name: ${product.product_name}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Price: ${product.price} Kč")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Quantity: ${product.quantity}")
            }
        }
    }
}