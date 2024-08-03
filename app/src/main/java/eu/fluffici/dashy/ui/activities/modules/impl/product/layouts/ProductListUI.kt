package eu.fluffici.dashy.ui.activities.modules.impl.product.layouts

import android.icu.text.SymbolTable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.CardOrderClickEvent
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.logs.PaginateButtons
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.CreateViewModel
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.CreateViewModelFactory
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.PartialProduct
import org.greenrobot.eventbus.EventBus

@Composable
fun ProductListUI(
    mBus: EventBus,
    onParentClick: () -> Unit = {}
) {
    val createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory()
    )

    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val products by createViewModel.products.collectAsState()
    val currentPage = remember { mutableIntStateOf(1) }

    LaunchedEffect(key1 = products) {
        try {
            // Simulate a fetch or handle side effects
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
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(error)
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(5.dp)
            ) {
                Column {
                    DashboardTitle(text = "Products", icon = R.drawable.square_arrow_left_svg, true) {
                        onParentClick()
                    }

                    if (products.isNotEmpty()) {
                        PaginateButtons(
                            onNextClick = {
                                currentPage.intValue += 1
                                isLoading.value = true
                                createViewModel.fetchProducts(currentPage.intValue) // Trigger fetch for new page
                            },
                            onPrevClick = {
                                currentPage.intValue -= 1
                                isLoading.value = true
                                createViewModel.fetchProducts(currentPage.intValue) // Trigger fetch for new page
                            },
                            currentPage = currentPage.intValue,
                            maxPages = products[0].maxPages
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(products) { prdel ->
                                    ProductItem(product = prdel, onProductCardClick = {
                                        mBus.post(CardOrderClickEvent(it))
                                    })
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No products available")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProductItem(
    product: PartialProduct,
    onProductCardClick: (id: String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onProductCardClick(product.id)
            },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = appFontFamily
                )
                Text(
                    text = getPrice(product.price),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}