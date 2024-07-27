package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.fluffici.calendar.shared.fetchVoucher
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Error
import eu.fluffici.dashy.entities.Voucher
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@Composable
fun VoucherInformationScreen(
    encodedData: String?,
    unrecognised: (error: Error) -> Unit = {},
    onParentClick: () -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val voucher = remember { mutableStateOf<Pair<Error?, Voucher?>>(Pair(null, null)) }

    LaunchedEffect(key1 = true) {
        try {
            voucher.value = fetchVoucher(encodedData)
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
            voucher.value.first?.let { unrecognised(it) }
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.error)
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp)
            ) {
                Column {
                    DashboardTitle(
                        text = "Voucher Information",
                        icon = R.drawable.square_arrow_left_svg,
                        isOnBeginning = true
                    ) {
                        onParentClick()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    voucher.value.second?.let {
                        VoucherCard(it)
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherCard(voucher: Voucher) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Customer Name: ${voucher.customer.first_name} ${voucher.customer.last_name}",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Amount: ${voucher.balance} Kč",
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Expiration: ${voucher.expireAt}",
                style = MaterialTheme.typography.body2
            )

            if (voucher.isRestricted) {
                VoucherInfo(icon = R.drawable.alert_triangle_svg, title = "Restricted")
            }

            if (voucher.isExpired) {
                VoucherInfo(icon = R.drawable.clock_exclamation_svg, title = "Expired")
            }
        }
    }
}

@Composable
fun VoucherInfo(
    icon: Int,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}