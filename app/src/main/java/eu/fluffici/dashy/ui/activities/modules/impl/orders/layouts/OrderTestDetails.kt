package eu.fluffici.dashy.ui.activities.modules.impl.orders.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.DashboardTitle

@Composable
fun OrderTest(
    onPaymentClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onParentClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(5.dp)) {

        Column {
            DashboardTitle(text = "Order from test", icon = R.drawable.square_arrow_left_filled_svg, true) {
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
                        Text(text = "Order Details")
                    }
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 }
                    ) {
                        Text(text = "Transactions")
                    }
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 }
                    ) {
                        Text(text = "Products")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                when (selectedTabIndex) {
                    0 -> {

                    }
                    1 -> {

                    }
                    2 -> {

                    }
                }
            }
        }
    }
}