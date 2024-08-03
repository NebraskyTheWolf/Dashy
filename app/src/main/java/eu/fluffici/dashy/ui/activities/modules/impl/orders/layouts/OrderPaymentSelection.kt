package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.fluffici.calendar.shared.fetchOrder
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.FullOrder
import eu.fluffici.dashy.entities.Order
import eu.fluffici.dashy.events.module.PostOrderPaymentEvent
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.components.Dialog
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import org.greenrobot.eventbus.EventBus

@Composable
fun PaymentMethodSelection(
    onCancelClick: () -> Unit = {},
    order: String,
    eventBus: EventBus
) {

    val orders = remember { mutableStateOf<FullOrder?>(null) }

    LaunchedEffect(key1 = true) {
        orders.value = fetchOrder(orderId = order)!!.getAllDetails()
    }

    orders.value?.let {
        var showCashConfirmation by remember { mutableStateOf(false) }
        var showVoucherConfirmation by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Text(
                text = "Select the payment method.",
                modifier = Modifier.padding(bottom = 16.dp),
                fontFamily = appFontFamily
            )

            PaymentMethodOption(
                text = "Cash",
                icon = R.drawable.cash_svg,
                onClick = { showCashConfirmation = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodOption(
                text = "Voucher",
                icon = R.drawable.receipt_svg,
                onClick = { showVoucherConfirmation = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancelClick) {
                    Text(text = "Cancel")
                }
            }
        }

        if (showCashConfirmation) {
            Dialog(
                title = "Confirm Cash Payment",
                message = "Are you sure you want to pay with cash?",
                onConfirm = {
                    eventBus.post(PostOrderPaymentEvent(it.order, "CASH"))
                    showCashConfirmation = false
                },
                onCancel = {
                    showCashConfirmation = false
                }
            )
        }

        if (showVoucherConfirmation) {
            Dialog(
                title = "Confirm Voucher Payment",
                message = "Are you sure you want to pay with a voucher?",
                onConfirm = {
                    eventBus.post(PostOrderPaymentEvent(it.order, "VOUCHER"))
                    showVoucherConfirmation = false
                },
                onCancel = {
                    showVoucherConfirmation = false
                }
            )
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    }
}

@Composable
fun PaymentMethodOption(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().background(Color.White).clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text,
                fontFamily = appFontFamily,
                color = Color.Black
            )
        }
    }
}