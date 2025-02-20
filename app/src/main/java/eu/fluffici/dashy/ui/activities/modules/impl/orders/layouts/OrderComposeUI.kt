package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.common.DashboardCard
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.DashboardUICard
import org.greenrobot.eventbus.EventBus

@Composable
fun OrderUI(eventBus: EventBus, onParentClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        DashboardTitle(text = "Order Management", icon = R.drawable.square_arrow_left_svg, true) {
            onParentClick()
        }

        Spacer(modifier = Modifier.height(20.dp).background(color = Color.White))
        OrdersGrid(eventBus)
    }
}

@Composable
fun OrdersGrid(eventBus: EventBus) {
    val orders = listOf(
        DashboardUICard("scan_order", icon = R.drawable.qrcode_svg, text = R.string.scan_order),
        DashboardUICard("orders_list", icon = R.drawable.list_details_svg, text = R.string.orders_list),
        DashboardUICard("voucher_info", icon = R.drawable.receipt_euro_svg, text = R.string.voucher_info)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(orders) { order ->
            DashboardCard(order, eventBus)
        }
    }
}