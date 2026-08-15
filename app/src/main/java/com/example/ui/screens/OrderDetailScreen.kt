package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.BillRow
import com.example.ui.components.TrackingTimelineView
import com.example.ui.components.formatCurrency
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    onAdvanceStatus: () -> Unit,
    onCancelOrder: (String) -> Unit,
    onReturnOrder: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("Changed mind / Ordered by mistake") }

    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReason by remember { mutableStateOf("Size/Fit issue or defective item") }

    var showInvoiceDialog by remember { mutableStateOf(false) }

    val statusColor = when (order.status) {
        OrderStatus.DELIVERED -> AccentGreen
        OrderStatus.CANCELLED, OrderStatus.RETURNED -> AccentRed
        else -> MaterialTheme.colorScheme.primary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInvoiceDialog = true }) {
                        Icon(Icons.Outlined.ReceiptLong, contentDescription = "View Invoice")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 30.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .testTag("order_detail_scroll")
        ) {
            // 1. Order Status Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Status: ${order.status.displayName}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = statusColor
                                    )
                                )
                                Text(
                                    text = "Expected Delivery by ${order.estimatedDeliveryDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = if (order.status == OrderStatus.DELIVERED) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Courier partner and tracking ID info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Courier Partner", style = MaterialTheme.typography.labelSmall)
                                Text(order.courierName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Tracking AWB", style = MaterialTheme.typography.labelSmall)
                                Text(order.trackingNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        // Simulation helper button: Advance Stage (for live testing tracking stages)
                        if (order.status.stepIndex in 0..4) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = onAdvanceStatus,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("advance_order_status_btn")
                            ) {
                                Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Advance Tracking Stage (Demo/Simulation)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Interactive Live Tracking Timeline
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Live Package Tracking",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        TrackingTimelineView(steps = order.trackingSteps)
                    }
                }
            }

            // 3. Action Buttons (Cancel / Return / Support)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    if (order.status.stepIndex in 0..2) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("cancel_order_btn")
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel Order")
                        }
                    }

                    if (order.status == OrderStatus.DELIVERED) {
                        OutlinedButton(
                            onClick = { showReturnDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("return_order_btn")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Return / Replace")
                        }
                    }

                    OutlinedButton(
                        onClick = { showInvoiceDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Outlined.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invoice")
                    }
                }
            }

            // 4. Items in this order
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items in Order (${order.items.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        order.items.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.brand, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Text(item.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), maxLines = 1)
                                    Text("Qty: ${item.quantity}  •  ${item.variant}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("₹${formatCurrency(item.price * item.quantity)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // 5. Shipping Address & Payment Summary
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Shipping Address", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(order.address.fullName, fontWeight = FontWeight.Bold)
                        Text("${order.address.line1}, ${order.address.city} - ${order.address.pincode}")
                        Text("Phone: ${order.address.phone}", style = MaterialTheme.typography.bodySmall)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Payment Summary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        BillRow("Payment Method", order.paymentMethod.name)
                        BillRow("Payment Status", order.paymentStatus, valueColor = AccentGreen)
                        BillRow("Items Subtotal", "₹${formatCurrency(order.subtotal)}")
                        if (order.discount > 0) {
                            BillRow("Discount Savings", "- ₹${formatCurrency(order.discount)}", valueColor = AccentGreen)
                        }
                        BillRow("Delivery", if (order.deliveryCharge == 0.0) "FREE" else "₹${formatCurrency(order.deliveryCharge)}")
                        BillRow("GST (18%)", "₹${formatCurrency(order.tax)}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        BillRow("Grand Total", "₹${formatCurrency(order.total)}", valueColor = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    // Cancel Order Dialog
    if (showCancelDialog) {
        val reasons = listOf(
            "Changed mind / Ordered by mistake",
            "Found a better price elsewhere",
            "Delivery time is too long",
            "Need to change shipping address"
        )
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order #${order.orderNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please choose a reason for cancellation:")
                    Spacer(modifier = Modifier.height(8.dp))
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { cancelReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = cancelReason == r, onClick = { cancelReason = r })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(r, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelOrder(cancelReason)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Confirm Cancel", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Order")
                }
            }
        )
    }

    // Return Order Dialog
    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Request Return / Exchange", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Return window is active for 7 days after delivery.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = returnReason,
                        onValueChange = { returnReason = it },
                        label = { Text("Reason for Return") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReturnOrder(returnReason)
                        showReturnDialog = false
                    }
                ) {
                    Text("Submit Return Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Download Invoice Dialog / Preview
    if (showInvoiceDialog) {
        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tax Invoice", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Invoice No: INV-2026-${order.orderNumber}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Text("GSTIN: 29AABCS1429B1ZB", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Billed To: ${order.address.fullName}")
                    Text("${order.address.line1}, ${order.address.city}")
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    order.items.forEach { itm ->
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("${itm.title.take(20)}... x${itm.quantity}", style = MaterialTheme.typography.bodySmall)
                            Text("₹${formatCurrency(itm.price * itm.quantity)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    BillRow("Total Taxable", "₹${formatCurrency(order.subtotal - order.discount)}")
                    BillRow("Integrated GST (18%)", "₹${formatCurrency(order.tax)}")
                    BillRow("Grand Total Paid", "₹${formatCurrency(order.total)}", valueColor = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                Button(onClick = { showInvoiceDialog = false }) {
                    Text("Download PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvoiceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
