package eu.fluffici.dashy.ui.activities.modules.impl.product.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.components.Dialog
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.DummyProduct
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.ProductViewModel
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.ProductViewModelFactory

@Composable
fun InventoryUI(onParentClick: () -> Unit = {}, executeToast: (slug: String, value: String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column {
            DashboardTitle(
                text = "Inventory",
                icon = R.drawable.square_arrow_left_filled_svg,
                true
            ) {
                onParentClick()
            }

            InventoryScreen(executeToast = executeToast)
        }
    }
}

@Composable
fun InventoryScreen(executeToast: (slug: String, value: String) -> Unit) {
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(LocalContext.current)
    )

    var showUnknownModal by remember { mutableStateOf(false) }
    var showErrorModal by remember { mutableStateOf(false) }

    val scannedProducts by productViewModel.scannedProducts.collectAsState()
    val unknownProduct by productViewModel.isUnknown.collectAsState()
    val getErrors by productViewModel.getErrors.collectAsState()
    val getSuccess by productViewModel.getSuccess.collectAsState()
    val getSaved by productViewModel.getSaved.collectAsState()

    LaunchedEffect(scannedProducts) {}
    LaunchedEffect(unknownProduct) {}
    LaunchedEffect(getErrors) {}

    if (unknownProduct.first) {
        showUnknownModal = true
    } else if (getErrors.first) {
        showErrorModal = true
    } else if (getSaved.get() > 0) {
        executeToast("inventory_saved", "Inventory saved with ${getSaved.get()} products.")
    } else if (getSuccess.first) {
        executeToast(getSuccess.second.slug, getSuccess.second.message)
        productViewModel.clearErrors()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (scannedProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please press SCAN to continue",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        productViewModel.simulate()
                    }
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(scannedProducts.size) { index ->
                    ProductCard(product = scannedProducts[index])
                }
            }
        }
        InventoryActions(
            onClear = { productViewModel.clearProducts() },
            onSave = { productViewModel.saveProducts() },
            onCheck = { productViewModel.checkProducts() }
        )
    }

    if (showUnknownModal) {
        Dialog(
            title = unknownProduct.second.title,
            message = unknownProduct.second.message,
            confirmText = "Ok",
            onConfirm = {
                showUnknownModal = false
                productViewModel.clearErrors()
            },
            onCancel = {},
            hasDismiss = false
        )
    }

    if (showErrorModal) {
        Dialog(
            title = getErrors.second.title,
            message = getErrors.second.message,
            confirmText = "Ok",
            onConfirm = {
                showErrorModal = false
                productViewModel.clearErrors()
            },
            onCancel = {},
            hasDismiss = false
        )
    }
}

@Composable
fun ProductCard(product: DummyProduct) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (product.product.productIcon !== null) {
                    AsyncImage(
                        model = "https://autumn.fluffici.eu/attachments/${product.product.productIcon}",
                        contentDescription = product.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column {
                    Text(product.product.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Quantity: ${product.quantity}", fontSize = 16.sp, color = Color.Gray)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Text("Scan Breakdown:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                    product.scanTimes.forEach { time ->
                        Text(time, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryActions(onClear: () -> Unit, onSave: () -> Unit, onCheck: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = { onClear(); expanded = false }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.trash_x_filled_svg), contentDescription = "Clear", modifier = Modifier.size(24.dp), tint = Color.Black)
                    Text("Clear", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
            DropdownMenuItem(onClick = { onSave(); expanded = false }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ballpen_filled_svg), contentDescription = "Save", modifier = Modifier.size(24.dp), tint = Color.Black)
                    Text("Save", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
            DropdownMenuItem(onClick = { onCheck(); expanded = false }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.cards_filled_svg), contentDescription = "Check", modifier = Modifier.size(24.dp), tint = Color.Black)
                    Text("Check", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.plus_svg), contentDescription = "Actions", modifier = Modifier.size(24.dp), tint = Color.Black)
                Text("Actions", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}