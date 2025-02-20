package eu.fluffici.dashy.ui.activities.modules.impl.product.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import eu.fluffici.calendar.shared.akceDateTimeFormatter
import eu.fluffici.calendar.shared.fetchProduct
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.ProductBody
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.getDeviceInfo
import eu.fluffici.dashy.showToast
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.modules.impl.logs.LoadingIndicator
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.CDummyProduct
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.CreateViewModel
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.CreateViewModelFactory
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.DummyCategory
import eu.fluffici.dashy.ui.activities.modules.impl.product.model.DummyProductSale
import eu.fluffici.dashy.ui.activities.modules.impl.users.NetworkImage
import org.greenrobot.eventbus.EventBus
import java.sql.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Currency
import java.util.Date
import java.util.Locale

val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("cs"))
fun getPrice(value: Double): String {
    currencyFormat.currency = Currency.getInstance("CZK")
    return currencyFormat.format(value)
}

fun getOrderStatus(value: String): String {
    return when(value) {
        "PENDING_APPROVAL" -> "Approval"
        "PROCESSING" -> "Processing"
        "CANCELLED", "DENIED" -> "Cancelled"
        "REFUNDED" -> "Refunded"
        "DISPUTED" -> "Disputed"
        "DELIVERED" -> "Delivered"
        "ARCHIVED" -> "Archived"
        "COMPLETED" -> "Completed"
        "OUTING" -> "Ready for pickup"
        "OUTING_DELIVERED" -> "Picked up"
        else -> value
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductDetailsUI(productId: String, eventBus: EventBus, onParentClick: () -> Unit = {}) {
    currencyFormat.currency = Currency.getInstance("CZK")

    val context = LocalContext.current
    var showCreateProduct by remember { mutableStateOf(false) }
    var showAddSalesDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf(false) }
    var showViewBarcodeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val product = remember { mutableStateOf< Pair<eu.fluffici.dashy.entities.Error?, ProductBody?>?>(null) }

    LaunchedEffect(key1 = true) {
        try {
            product.value = fetchProduct(upcCode = productId)
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
                .background(Color.Black), contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    } else {
        errorMessage.value?.let { error ->
            ErrorScreen(
                title = "Application error",
                description = error,
                onParentClick = {
                    onParentClick()
                }
            )
        } ?: run {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(5.dp)
            ) {
                val maxWidth = maxWidth
                val isCompact = maxWidth < 600.dp

                Column {
                   if (!context.getDeviceInfo().isPDADevice) {
                       DashboardTitle(
                           text = "Product Management",
                           icon = R.drawable.square_arrow_left_filled_svg,
                           isOnBeginning = true,
                           isCompact = isCompact
                       ) {
                           onParentClick()
                       }

                       Spacer(modifier = Modifier.height(20.dp).background(color = Color.White))
                   }

                    if (product.value?.second != null) {
                        val product: ProductBody = product.value?.second!!

                        if (showAddSalesDialog)
                            AddSalesDialog(onDismiss = { eventBus.post(CardClickEvent("refresh_${productId}")) }, product = product)
                        if (showEditProductDialog)
                            EditProductDialog(onDismiss = { eventBus.post(CardClickEvent("refresh_${productId}")) }, product = product)
                        if (showViewBarcodeDialog)
                            ViewBarcodeDialog(onDismiss = { showViewBarcodeDialog = false }, product = product)
                        if (showDeleteConfirmationDialog)
                            DeleteConfirmationDialog(onDismiss = { onParentClick() }, product = product)

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            elevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                if (product.productIcon != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        NetworkImage(product = product)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.Gray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No Image Available",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.h6.copy(fontSize = if (isCompact) 16.sp else 20.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontFamily = appFontFamily
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.body1.copy(fontSize = if (isCompact) 14.sp else 16.sp),
                                    color = Color.Gray,
                                    fontFamily = appFontFamily
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Price",
                                            style = MaterialTheme.typography.caption.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Gray,
                                            fontFamily = appFontFamily
                                        )
                                        Text(
                                            text = currencyFormat.format(product.price),
                                            style = MaterialTheme.typography.body1.copy(fontSize = if (isCompact) 14.sp else 16.sp),
                                            color = Color.Black,
                                            fontFamily = appFontFamily
                                        )
                                    }

                                    val displayStatus = if (product.displayed == 1) "Public" else "Private"

                                    Column {
                                        Text(
                                            text = "Status",
                                            style = MaterialTheme.typography.caption.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Gray,
                                            fontFamily = appFontFamily
                                        )
                                        Text(
                                            text = displayStatus,
                                            style = MaterialTheme.typography.body1.copy(fontSize = if (isCompact) 14.sp else 16.sp),
                                            color = if (product.displayed == 1) Color.Green else Color.Red,
                                            fontFamily = appFontFamily
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Quantity",
                                            style = MaterialTheme.typography.caption.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Gray,
                                            fontFamily = appFontFamily
                                        )
                                        Text(
                                            text = NumberFormat.getNumberInstance()
                                                .format(product.quantity),
                                            style = MaterialTheme.typography.body1.copy(fontSize = if (isCompact) 14.sp else 16.sp),
                                            color = Color.Black,
                                            fontFamily = appFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Created At",
                                            style = MaterialTheme.typography.caption.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Gray,
                                            fontFamily = appFontFamily
                                        )
                                        Text(
                                            text = akceDateTimeFormatter.format(
                                                Timestamp.valueOf(
                                                    product.createdAt.replace(
                                                        "T",
                                                        " "
                                                    ).replace(".000000Z", "")
                                                )
                                            ).toString(),
                                            style = MaterialTheme.typography.body2.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Black,
                                            fontFamily = appFontFamily
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Updated At",
                                            style = MaterialTheme.typography.caption.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Gray,
                                            fontFamily = appFontFamily
                                        )
                                        Text(
                                            text = akceDateTimeFormatter.format(
                                                Timestamp.valueOf(
                                                    product.updatedAt.replace(
                                                        "T",
                                                        " "
                                                    ).replace(".000000Z", "")
                                                )
                                            ).toString(),
                                            style = MaterialTheme.typography.body2.copy(fontSize = if (isCompact) 12.sp else 14.sp),
                                            color = Color.Black,
                                            fontFamily = appFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                var expanded by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    IconButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier
                                            .background(Color.Gray, shape = CircleShape)
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.plus_svg),
                                            contentDescription = "More Options",
                                            tint = Color.White
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Color.White),
                                    ) {
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            showAddSalesDialog = true
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.receipt_tax_svg),
                                                contentDescription = null,
                                                tint = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Add Sales")
                                        }
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            showEditProductDialog = true
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.edit_svg),
                                                contentDescription = null,
                                                tint = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Edit Product")
                                        }
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            showViewBarcodeDialog = true
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.file_barcode_svg),
                                                contentDescription = null,
                                                tint = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("View Barcode")
                                        }
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            showDeleteConfirmationDialog = true
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.trash_svg),
                                                contentDescription = null,
                                                tint = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        CreateProductDialog(upcId = productId, onDismiss = { onParentClick() }) {
                            eventBus.post(CardClickEvent("refresh_${productId}"))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddSalesDialog(onDismiss: () -> Unit, product: ProductBody) {

    val context = LocalContext.current
    val createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory()
    )

    var discount by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }

    CustomDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Sales", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Discount (%)", style = MaterialTheme.typography.caption)
            TextField(
                value = discount,
                onValueChange = { discount = it },
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Expiration Date", style = MaterialTheme.typography.caption)
            DatePicker(
                onValueChange = { expirationDate = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    createViewModel.createSale(DummyProductSale(
                        product = product,
                        reduction = discount.toDouble(),
                        expiration = expirationDate
                    )).let {
                        context.showToast("Sales added on ${product.name}")
                        onDismiss()
                    }
                }) {
                    Text("Apply")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditProductDialog(onDismiss: () -> Unit, product: ProductBody) {
    val createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory()
    )

    val getErrors by createViewModel.getErrors.collectAsState()

    LaunchedEffect(getErrors) {}

    val context = LocalContext.current
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableDoubleStateOf(product.price) }
    var status by remember { mutableStateOf(if (product.displayed == 1) "Public" else "Private") } // Default to "Public"
    var statusExpanded by remember { mutableStateOf(false) }

    CustomDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Edit a product", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Name", style = MaterialTheme.typography.caption)
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Description", style = MaterialTheme.typography.caption)
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Price", style = MaterialTheme.typography.caption)
            TextField(
                value = price.toString(),
                onValueChange = { price = it.toDouble() },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Status", style = MaterialTheme.typography.caption)
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                TextField(
                    value = status,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { statusExpanded = true },
                    textStyle = MaterialTheme.typography.body2
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        status = "Public"
                        statusExpanded = false
                    }) {
                        Text("Public")
                    }
                    DropdownMenuItem(onClick = {
                        status = "Private"
                        statusExpanded = false
                    }) {
                        Text("Private")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    createViewModel.updateProduct(product = product.copy(
                        name = name,
                        description = description,
                        price = price,
                        displayed = if (status == "Public") 1 else 0
                    )).let {
                        onDismiss()
                        context.showToast("Product $name has been updated.")
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateProductDialog(upcId: String, onDismiss: () -> Unit, onCreate: () -> Unit) {
    val createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory()
    )

    // Category data : id, name
    val categories by createViewModel.categories.collectAsState()
    val getErrors by createViewModel.getErrors.collectAsState()

    LaunchedEffect(categories) {}
    LaunchedEffect(getErrors) {}

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Public") } // Default to "Public"
    var statusExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf(DummyCategory(0, "Select a category")) }
    var categoryExpanded by remember { mutableStateOf(false) }

    CustomDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Create a new Product", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Name", style = MaterialTheme.typography.caption)
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Description", style = MaterialTheme.typography.caption)
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Price", style = MaterialTheme.typography.caption)
            TextField(
                value = price,
                onValueChange = { price = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Status", style = MaterialTheme.typography.caption)
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                TextField(
                    value = status,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { statusExpanded = true },
                    textStyle = MaterialTheme.typography.body2
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        status = "Public"
                        statusExpanded = false
                    }) {
                        Text("Public")
                    }
                    DropdownMenuItem(onClick = {
                        status = "Private"
                        statusExpanded = false
                    }) {
                        Text("Private")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Category", style = MaterialTheme.typography.caption)
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                TextField(
                    value = selectedCategory.name,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true },
                    textStyle = MaterialTheme.typography.body2
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }) {
                            Text(category.name)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    createViewModel.createProduct(CDummyProduct(
                        upcId,
                        name,
                        selectedCategory.id,
                        description,
                        price,
                        status
                    )).let {
                        context.showToast("Product $name has been created.")
                    }

                    onCreate()
                }) {
                    Text("Create")
                }
            }
        }
    }
}

@Composable
fun ViewBarcodeDialog(onDismiss: () -> Unit, product: ProductBody) {
    CustomDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("View Barcode", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            if (product.hasUpc == 1)
                BarcodeComposableCustom(upcCode = product.upcCode!!)
            else
                BarcodeComposable(upcCode = product.id)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onDismiss: () -> Unit, product: ProductBody) {
    val context = LocalContext.current
    val createViewModel: CreateViewModel = viewModel(
        factory = CreateViewModelFactory()
    )

    CustomDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Delete Product", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Are you sure you want to delete ${product.name}?", style = MaterialTheme.typography.body2, fontFamily = appFontFamily)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", fontFamily = appFontFamily)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    createViewModel.deleteProduct(product).let {
                        context.showToast("${product.name} has been deleted.")
                    }
                    onDismiss()
                }) {
                    Text("Delete", fontFamily = appFontFamily)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    onValueChange: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.forLanguageTag("cs")) }

    var date by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(true) }

    if (showPicker) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WheelDateTimePicker(
                startDateTime = LocalDateTime.now(),
                minDateTime = LocalDateTime.now(),
                maxDateTime = LocalDateTime.now().plusYears(10),
                timeFormat = TimeFormat.AM_PM,
                size = DpSize(200.dp, 100.dp),
                rowCount = 5,
                textStyle = MaterialTheme.typography.body2,
                textColor = Color.Black,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = true,
                    shape = RectangleShape,
                    color = Color(0xFFf1faee).copy(alpha = 0.2f),
                    border = BorderStroke(2.dp, Color(0xFFf1faee))
                )
            ) { snappedDateTime ->
                date = dateFormat.format(
                    Date.from(snappedDateTime.atZone(ZoneId.systemDefault()).toInstant())
                )
                onValueChange(date)
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {
                    showPicker = false
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.check_svg), contentDescription = "Save")
            }
        }
    } else {
        TextField(
            value = date,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            textStyle = TextStyle.Default.copy(color = Color.White),
            readOnly = true
        )
    }
}

@Composable
fun BarcodeComposable(upcCode: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://api.fluffici.eu/api/product/ean?productId=${generateUPCA(upcCode)}",
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(400.dp)
        )
    }
}

@Composable
fun BarcodeComposableCustom(upcCode: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://api.fluffici.eu/api/product/ean?productId=${upcCode}",
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(400.dp)
        )
    }
}

@Composable
fun CustomDialog(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .clickable(indication = null, interactionSource = interactionSource) {}
        ) {
            content()
        }
    }
}

fun generateUPCA(id: Int): String {
    val code = id.toString().padStart(11, '0')
    var oddSum = 0
    var evenSum = 0
    for (i in code.indices) {
        if ((i + 1) % 2 == 0) {
            evenSum += code[i].toString().toInt()
        } else {
            oddSum += code[i].toString().toInt()
        }
    }
    val totalSum = evenSum + (3 * oddSum)
    val checksum = (10 - (totalSum % 10)) % 10
    val upca = code + checksum

    if (upca.length != 12) {
        throw IllegalArgumentException("UPC-A code should be 12 digits long ( ${upca.length} found, checksum: $checksum / $totalSum <-!-> $oddSum == $code )")
    }

    return upca
}
