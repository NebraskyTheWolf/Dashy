package eu.fluffici.dashy.ui.activities.modules.impl.product.layouts

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
fun ProductUI(eventBus: EventBus, onParentClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        DashboardTitle(text = "Product(s) Management", icon = R.drawable.square_arrow_left_svg, true) {
            onParentClick()
        }

        Spacer(modifier = Modifier.height(20.dp).background(color = Color.White))
        ProductGrid(eventBus)
    }
}

@Composable
fun ProductGrid(eventBus: EventBus) {
    val orders = listOf(
        DashboardUICard("scan_product", icon = R.drawable.barcode_svg, text = R.string.product_scan),
        DashboardUICard("product_list", icon = R.drawable.list_details_svg, text = R.string.product_list),
        DashboardUICard("inventory", icon = R.drawable.box_svg, text = R.string.inventory, isPDA = true)
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