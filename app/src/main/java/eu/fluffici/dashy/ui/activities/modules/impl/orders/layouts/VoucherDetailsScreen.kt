package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.fluffici.calendar.shared.fetchVoucher
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.Error
import eu.fluffici.dashy.entities.Voucher
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator

@RequiresApi(Build.VERSION_CODES.O)
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
            if (voucher.value.second != null) {
                isLoading.value = false
            } else {
                unrecognised(voucher.value.first!!)
            }
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
                    DashboardTitle(text = "Voucher Information", icon = R.drawable.square_arrow_left_svg, true) {
                        onParentClick()
                    }

                    VoucherCard(voucher.value.second)
                }
            }
        }
    }
}

@Composable
fun VoucherCard(voucher: Voucher?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Customer Name: ${voucher?.customer?.first_name}, ${voucher?.customer?.last_name}",
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Amount: ${voucher?.balance} Kč",
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Expiration: ${voucher?.expireAt}",
                style = MaterialTheme.typography.body1
            )

            if (voucher?.isRestricted == true) {
                VoucherInfo(icon = R.drawable.alert_triangle_svg, title = "Restricted")
            }

            if (voucher?.isExpired == true) {
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = Color.Red
        )
    }
}