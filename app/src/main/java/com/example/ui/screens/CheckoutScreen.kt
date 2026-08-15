package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Address
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.ui.components.formatCurrency
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.viewmodel.CartSummaryState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    cartSummary: CartSummaryState,
    appliedCoupon: Coupon?,
    savedAddresses: List<Address>,
    selectedAddress: Address?,
    onSelectAddress: (Address) -> Unit,
    onAddNewAddress: (Address) -> Unit,
    onPlaceOrder: (PaymentMethod, (Order) -> Unit) -> Unit,
    onTrackOrder: (String) -> Unit,
    onContinueShopping: () -> Unit,
    onBackClick: () -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) } // 1: Address, 2: Review, 3: Payment, 4: Confirmed
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.UPI) }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var confirmedOrder by remember { mutableStateOf<Order?>(null) }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentStep < 4) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentStep) {
                                1 -> "Delivery Address"
                                2 -> "Review Order"
                                3 -> "Select Payment"
                                else -> "Order Confirmed"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep > 1) currentStep-- else onBackClick()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        bottomBar = {
            if (currentStep < 4) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Total Payable", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "₹${formatCurrency(cartSummary.total)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = {
                                if (currentStep == 1) {
                                    currentStep = 2
                                } else if (currentStep == 2) {
                                    currentStep = 3
                                } else if (currentStep == 3) {
                                    isProcessingPayment = true
                                    onPlaceOrder(selectedPaymentMethod) { order ->
                                        isProcessingPayment = false
                                        confirmedOrder = order
                                        currentStep = 4
                                    }
                                }
                            },
                            enabled = !isProcessingPayment && (currentStep != 1 || selectedAddress != null),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("checkout_continue_btn")
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processing...")
                            } else {
                                Text(
                                    text = when (currentStep) {
                                        1 -> "Proceed to Review"
                                        2 -> "Proceed to Payment"
                                        3 -> if (selectedPaymentMethod == PaymentMethod.COD) "Place Order (COD)" else "Pay ₹${formatCurrency(cartSummary.total)}"
                                        else -> "Done"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 20.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .testTag("checkout_screen_list")
        ) {
            // Step Wizard Indicator (Steps 1, 2, 3)
            if (currentStep < 4) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        listOf("1. Address", "2. Review", "3. Payment").forEachIndexed { idx, label ->
                            val stepNum = idx + 1
                            val isCurrent = currentStep == stepNum
                            val isDone = currentStep > stepNum

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) AccentGreen else if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    } else {
                                        Text(
                                            text = "$stepNum",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            // STEP 1: Address Selection
            if (currentStep == 1) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Select Delivery Address",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedButton(
                            onClick = { showAddAddressDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_new_address_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New")
                        }
                    }
                }

                items(savedAddresses) { addr ->
                    val isSelected = selectedAddress?.id == addr.id
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onSelectAddress(addr) }
                            .testTag("address_card_${addr.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectAddress(addr) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = addr.fullName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(addr.label.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${addr.line1}, ${addr.line2}", style = MaterialTheme.typography.bodySmall)
                                Text("${addr.city}, ${addr.state} - ${addr.pincode}", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Phone: ${addr.phone}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }

            // STEP 2: Order Review
            if (currentStep == 2) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Deliver To",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (selectedAddress != null) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(selectedAddress.fullName, fontWeight = FontWeight.Bold)
                                    Text("${selectedAddress.line1}, ${selectedAddress.city} - ${selectedAddress.pincode}")
                                    Text("Phone: ${selectedAddress.phone}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Items in Order (${cartItems.sumOf { it.quantity }})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }

                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.title, maxLines = 1, fontWeight = FontWeight.SemiBold)
                                Text("Qty: ${item.quantity}  •  ₹${formatCurrency(item.product.price)} each", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                "₹${formatCurrency(item.product.price * item.quantity)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Payment Summary", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            BillRow("Items Subtotal", "₹${formatCurrency(cartSummary.subtotal)}")
                            if (cartSummary.discount > 0) {
                                BillRow("Discount Savings", "- ₹${formatCurrency(cartSummary.discount)}", valueColor = AccentGreen)
                            }
                            BillRow("Delivery", if (cartSummary.deliveryCharge == 0.0) "FREE" else "₹${formatCurrency(cartSummary.deliveryCharge)}")
                            BillRow("GST (18%)", "₹${formatCurrency(cartSummary.tax)}")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            BillRow("Total", "₹${formatCurrency(cartSummary.total)}", valueColor = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // STEP 3: Payment Options
            if (currentStep == 3) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Choose Payment Option",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // 1. UPI Payment Option
                        PaymentMethodTile(
                            title = "Instant UPI (Google Pay, PhonePe, Paytm)",
                            subtitle = "Zero transaction fee & instant refund",
                            isSelected = selectedPaymentMethod == PaymentMethod.UPI,
                            icon = Icons.Default.AccountBalanceWallet,
                            onClick = { selectedPaymentMethod = PaymentMethod.UPI }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                listOf("Google Pay", "PhonePe", "Paytm", "BHIM").forEach { upiName ->
                                    val isChosen = selectedUpiApp == upiName
                                    FilterChip(
                                        selected = isChosen,
                                        onClick = { selectedUpiApp = upiName },
                                        label = { Text(upiName, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // 2. Cards Option
                        PaymentMethodTile(
                            title = "Credit / Debit Card",
                            subtitle = "Visa, MasterCard, RuPay & Amex",
                            isSelected = selectedPaymentMethod == PaymentMethod.CARD,
                            icon = Icons.Outlined.CreditCard,
                            onClick = { selectedPaymentMethod = PaymentMethod.CARD }
                        )


                        // 3. Net Banking
                        PaymentMethodTile(
                            title = "Net Banking",
                            subtitle = "All Indian Banks Supported",
                            isSelected = selectedPaymentMethod == PaymentMethod.NET_BANKING,
                            icon = Icons.Default.AccountBalance,
                            onClick = { selectedPaymentMethod = PaymentMethod.NET_BANKING }
                        )

                        // 4. Cash on Delivery
                        PaymentMethodTile(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay with cash or UPI at your doorstep",
                            isSelected = selectedPaymentMethod == PaymentMethod.COD,
                            icon = Icons.Default.Payments,
                            onClick = { selectedPaymentMethod = PaymentMethod.COD }
                        )
                    }
                }
            }

            // STEP 4: Order Confirmed Celebration
            if (currentStep == 4 && confirmedOrder != null) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .testTag("order_confirmed_view")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Order Placed Successfully",
                                tint = Color.White,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Order Placed Successfully!",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Order #${confirmedOrder!!.orderNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = "Estimated Delivery: ${confirmedOrder!!.estimatedDeliveryDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentGreen,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Summary Box
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Delivery To:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${confirmedOrder!!.address.fullName}, ${confirmedOrder!!.address.line1}, ${confirmedOrder!!.address.city} (${confirmedOrder!!.address.pincode})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Total Paid:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "₹${formatCurrency(confirmedOrder!!.total)} via ${confirmedOrder!!.paymentMethod.name}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onTrackOrder(confirmedOrder!!.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("track_confirmed_order_btn")
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Track Order Live Status", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onContinueShopping,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Continue Shopping")
                        }
                    }
                }
            }
        }
    }

    // Add New Address Modal Dialog
    if (showAddAddressDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var line1 by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("") }
        var pincode by remember { mutableStateOf("") }
        var label by remember { mutableStateOf("Home") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("10-digit Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = line1,
                        onValueChange = { line1 = it },
                        label = { Text("Flat, House no., Building, Street") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(end = 4.dp, top = 4.dp, bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("Pincode") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank() && line1.isNotBlank() && pincode.isNotBlank()) {
                            val newAddr = Address(
                                id = "addr_${System.currentTimeMillis()}",
                                fullName = name,
                                phone = phone,
                                line1 = line1,
                                line2 = "",
                                city = city.ifEmpty { "Bangalore" },
                                state = state.ifEmpty { "Karnataka" },
                                pincode = pincode,
                                label = label,
                                isDefault = true
                            )
                            onAddNewAddress(newAddr)
                            showAddAddressDialog = false
                        }
                    }
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PaymentMethodTile(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    extraContent: @Composable (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(selected = isSelected, onClick = onClick)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (isSelected && extraContent != null) {
                extraContent()
            }
        }
    }
}
