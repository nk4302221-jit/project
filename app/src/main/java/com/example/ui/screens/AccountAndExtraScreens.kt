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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed

@Composable
fun AccountScreen(
    user: UserProfile?,
    savedAddressesCount: Int,
    ordersCount: Int,
    notificationsCount: Int,
    appLanguage: String,
    onNavigateOrders: () -> Unit,
    onNavigateAddresses: () -> Unit,
    onNavigateWishlist: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSupport: () -> Unit,
    onShowAuthDialog: () -> Unit,
    onSignOut: () -> Unit,
    onChangeLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen_scroll")
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = (user?.name?.firstOrNull() ?: 'G').toString().uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "Guest User",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (user?.email?.isNotEmpty() == true) user.email else "Sign in to sync your cart & orders",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (user?.isGuest != false) {
                            Button(
                                onClick = onShowAuthDialog,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("login_signup_cta_btn")
                            ) {
                                Text("Sign In / Sign Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats Row (Bento 3-Card layout)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickStatCard(
                    title = "Orders",
                    value = "$ordersCount",
                    icon = Icons.Default.LocalShipping,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateOrders,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Wishlist",
                    value = "Saved",
                    icon = Icons.Default.Favorite,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateWishlist,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Addresses",
                    value = "$savedAddressesCount",
                    icon = Icons.Default.LocationOn,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = onNavigateAddresses,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Settings & Options List
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    AccountOptionRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications & Alerts",
                        badge = if (notificationsCount > 0) "$notificationsCount new" else null,
                        onClick = onNavigateNotifications
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    AccountOptionRow(
                        icon = Icons.Outlined.Headphones,
                        title = "24x7 Customer Help & FAQs",
                        onClick = onNavigateSupport
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    AccountOptionRow(
                        icon = Icons.Outlined.Translate,
                        title = "App Language",
                        trailingText = if (appLanguage == "hi") "हिन्दी (Hindi)" else "English",
                        onClick = { onChangeLanguage(if (appLanguage == "en") "hi" else "en") }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    AccountOptionRow(
                        icon = Icons.Outlined.Security,
                        title = "Privacy & Security Policy",
                        onClick = {}
                    )

                    if (user?.isGuest == false) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        AccountOptionRow(
                            icon = Icons.Default.Logout,
                            title = "Sign Out",
                            titleColor = AccentRed,
                            onClick = onSignOut
                        )
                    }
                }
            }
        }

        // App Version
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Text(
                    text = "ShopWave v2.4.0 • Modern E-Commerce Experience",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Firebase Auth & Realtime Firestore Integration Active",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AccentGreen
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


@Composable
fun AccountOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingText: String? = null,
    badge: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = titleColor)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp))
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (trailingText != null) {
                Text(text = trailingText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

// Notifications Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(

    notifications: List<AppNotification>,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications & Deals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all read")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                Text("No notifications right now", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications) { notif ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNotificationClick(notif) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "ORDER" -> AccentGreen
                                            "PRICE_DROP" -> AccentRed
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "ORDER" -> Icons.Default.LocalShipping
                                        "PRICE_DROP" -> Icons.Default.LocalFireDepartment
                                        else -> Icons.Default.LocalOffer
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Support & Help Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(

    tickets: List<SupportTicket>,
    onCreateTicket: (String?, String, String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFaqIndex by remember { mutableStateOf<Int?>(null) }
    var showTicketDialog by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            "How can I track my shipment?" to "You can view real-time live milestone tracking directly from the 'Orders' tab. Click on 'Track & Details' on any active order to see the courier tracking number and timeline.",
            "What is the return and replacement policy?" to "ShopWave offers a 7-day hassle-free return and replacement policy for all electronics, apparel, and footwear. Simply tap 'Return / Replace' on delivered orders.",
            "Which payment methods are accepted?" to "We accept UPI (Google Pay, PhonePe, Paytm, BHIM), all major Credit/Debit cards, Net Banking, and Cash on Delivery.",
            "How do promo coupons work?" to "Enter valid coupon codes like WAVE20 or WELCOME50 at checkout or cart screen to enjoy instant discounts."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Support & FAQs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 20.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Live Support Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Need Instant Assistance?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Our support executives are available 24/7 to resolve queries regarding orders, refunds, and delivery.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showTicketDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create Support Ticket")
                        }
                    }
                }
            }

            // Existing Tickets (if any)
            if (tickets.isNotEmpty()) {
                item {
                    Text("Your Active Tickets", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                items(tickets) { ticket ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(ticket.subject, fontWeight = FontWeight.Bold)
                                Text(ticket.status, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                            Text(ticket.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (ticket.replies.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Support: ${ticket.replies.first()}", style = MaterialTheme.typography.labelSmall, color = AccentGreen)
                            }
                        }
                    }
                }
            }

            // FAQs List
            item {
                Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            items(faqs.indices.toList()) { index ->
                val (q, a) = faqs[index]
                val isExpanded = selectedFaqIndex == index

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedFaqIndex = if (isExpanded) null else index }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = q, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = a, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showTicketDialog) {
        var subject by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Order & Delivery") }
        var message by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTicketDialog = false },
            title = { Text("Raise Support Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Describe your issue in detail") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank() && message.isNotBlank()) {
                            onCreateTicket(null, subject, category, message)
                            showTicketDialog = false
                        }
                    }
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTicketDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Authentication Modal Dialog (Google Sign-In + Email/Password)
@Composable
fun AuthDialog(
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: (String, String) -> Unit,
    onEmailSignUp: (String, String, String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSignUpTab by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSignUpTab) "Create ShopWave Account" else "Welcome Back",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sign in to save items across devices, track live orders, and unlock exclusive discounts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign In Button
                Button(
                    onClick = {
                        onGoogleSignIn()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("google_signin_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sign in with Google", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(" or with email ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSignUpTab) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (isSignUpTab) {
                            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                onEmailSignUp(name, email, password)
                                onDismiss()
                            }
                        } else {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onEmailSignIn(email, password)
                                onDismiss()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("email_auth_btn")
                ) {
                    Text(if (isSignUpTab) "Create Account" else "Sign In", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = { isSignUpTab = !isSignUpTab },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isSignUpTab) "Already have an account? Sign In" else "New to ShopWave? Create an account",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = {
                onContinueAsGuest()
                onDismiss()
            }) {
                Text("Continue as Guest")
            }
        }
    )
}
