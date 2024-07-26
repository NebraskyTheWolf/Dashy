package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.fluffici.calendar.shared.makeTypedPayment
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.components.Dialog

enum class TransactionType(val type: String) {
    CASH("CASH"), VOUCHER("VOUCHER")
}

data class TransactionBody(
    val order: String,
    val transactionType: TransactionType,
    val voucherBody: String
)

@Composable
fun PaymentInProgressScreen(
    transaction: TransactionBody,
    onSuccessConfirm: () -> Unit = {},
    onFailureConfirm: () -> Unit = {},
) {
    var transactionStatus by remember { mutableStateOf(Pair<String?, String?>(null, null)) }

    LaunchedEffect(key1 = true) {
        transactionStatus = makeTypedPayment(orderId = transaction.order, transaction.transactionType.type, transaction.voucherBody)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        Column {
            if (transactionStatus.first != null) {
                DashboardTitle(text = "Go back", icon = R.drawable.square_arrow_left_svg, true) {}
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Processing...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = appFontFamily
                    )
                }
            }
        }

        if (transactionStatus.second != null) {
            Dialog(
                title = "Payment success.",
                message = transactionStatus.second.toString(),
                onConfirm = onSuccessConfirm,
                onCancel = { },
                hasDismiss = false
            )
        }

        if (transactionStatus.first != null) {
            Dialog(
                title = "Payment failed.",
                message = transactionStatus.first.toString(),
                onConfirm = onFailureConfirm,
                onCancel = { },
                hasDismiss = false
            )
        }

        if (transactionStatus.first == null && transactionStatus.second == null) {
            Dialog(
                title = "Payment failed.",
                message = "Unable to contact Fluffici servers.",
                onConfirm = onFailureConfirm,
                onCancel = { },
                hasDismiss = false
            )
        }
    }
}