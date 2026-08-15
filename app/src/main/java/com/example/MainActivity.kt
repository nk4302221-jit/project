package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Address
import com.example.model.Product
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.ShopWaveTheme
import com.example.ui.viewmodel.ShopWaveViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: ShopWaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShopWaveTheme {
                ShopWaveApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopWaveApp(viewModel: ShopWaveViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State Collection
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val filterCriteria by viewModel.filterCriteria.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.productsList.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val wishlistProductIds by viewModel.wishlistProductIds.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val detailedWishlistItems by viewModel.detailedWishlistItems.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.savedAddresses.collectAsStateWithLifecycle()
    val selectedAddress by viewModel.selectedAddress.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val comparedProductIds by viewModel.comparedProductIds.collectAsStateWithLifecycle()
    val pincodeResult by viewModel.pincodeResult.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val supportTickets by viewModel.supportTickets.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    // Navigation Sub-States
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var selectedOrderId by remember { mutableStateOf<String?>(null) }
    var isCheckingOut by remember { mutableStateOf(false) }
    var isViewingNotifications by remember { mutableStateOf(false) }
    var isViewingSupport by remember { mutableStateOf(false) }
    var isViewingCompare by remember { mutableStateOf(false) }
    var isViewingWishlist by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showAddressSheet by remember { mutableStateOf(false) }

    // Toast/Snackbar trigger
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Hardware Back Button Handling
    BackHandler(
        enabled = selectedProductId != null || selectedOrderId != null || isCheckingOut ||
                isViewingNotifications || isViewingSupport || isViewingCompare || isViewingWishlist || currentDestination != NavDestination.HOME
    ) {
        when {
            selectedProductId != null -> selectedProductId = null
            selectedOrderId != null -> selectedOrderId = null
            isCheckingOut -> isCheckingOut = false
            isViewingNotifications -> isViewingNotifications = false
            isViewingSupport -> isViewingSupport = false
            isViewingCompare -> isViewingCompare = false
            isViewingWishlist -> isViewingWishlist = false
            currentDestination != NavDestination.HOME -> currentDestination = NavDestination.HOME
        }
    }

    val selectedProduct = remember(selectedProductId, products) {
        selectedProductId?.let { id -> viewModel.getProductById(id) }
    }

    val selectedOrder = remember(selectedOrderId, orders) {
        selectedOrderId?.let { id -> orders.find { it.id == id } }
    }

    val comparedProducts = remember(comparedProductIds, products) {
        comparedProductIds.mapNotNull { id -> viewModel.getProductById(id) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Tablet / Desktop Side Navigation Rail
            if (isWideScreen && selectedProduct == null && selectedOrder == null && !isCheckingOut) {
                ShopWaveNavRail(
                    currentDestination = currentDestination,
                    cartItemCount = cartItemCount,
                    onNavigate = { dest ->
                        currentDestination = dest
                        selectedProductId = null
                        selectedOrderId = null
                        isCheckingOut = false
                        isViewingNotifications = false
                        isViewingSupport = false
                        isViewingCompare = false
                        isViewingWishlist = false
                    }
                )
            }

            // Main Content Area
            Scaffold(
                topBar = {
                    if (selectedProduct == null && selectedOrder == null && !isCheckingOut &&
                        !isViewingNotifications && !isViewingSupport && !isViewingCompare && !isViewingWishlist
                    ) {
                        ShopWaveTopBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { query ->
                                viewModel.onSearchQueryChange(query)
                                if (query.isNotEmpty() && currentDestination != NavDestination.SEARCH) {
                                    currentDestination = NavDestination.SEARCH
                                }
                            },
                            onSearchSubmit = { query ->
                                viewModel.submitSearch(query)
                                currentDestination = NavDestination.SEARCH
                            },
                            onSearchClick = { currentDestination = NavDestination.SEARCH },
                            cartItemCount = cartItemCount,
                            unreadNotificationCount = unreadNotificationCount,
                            comparedCount = comparedProductIds.size,
                            selectedAddress = selectedAddress,
                            onAddressClick = { showAddressSheet = true },
                            onCartClick = { currentDestination = NavDestination.CART },
                            onNotificationClick = { isViewingNotifications = true },
                            onCompareClick = { isViewingCompare = true }
                        )
                    }
                },
                bottomBar = {
                    if (!isWideScreen && selectedProduct == null && selectedOrder == null && !isCheckingOut &&
                        !isViewingNotifications && !isViewingSupport && !isViewingCompare && !isViewingWishlist
                    ) {
                        ShopWaveBottomNavBar(
                            currentDestination = currentDestination,
                            cartItemCount = cartItemCount,
                            onNavigate = { dest ->
                                currentDestination = dest
                                selectedProductId = null
                                selectedOrderId = null
                                isCheckingOut = false
                                isViewingNotifications = false
                                isViewingSupport = false
                                isViewingCompare = false
                                isViewingWishlist = false
                            }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier.weight(1f)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when {
                        // 1. Product Detail Screen
                        selectedProduct != null -> {
                            ProductDetailScreen(
                                product = selectedProduct,
                                relatedProducts = viewModel.getRelatedProducts(selectedProduct),
                                reviews = reviews,
                                isInWishlist = wishlistProductIds.contains(selectedProduct.id),
                                isCompared = comparedProductIds.contains(selectedProduct.id),
                                pincodeCheckResult = pincodeResult,
                                onCheckPincode = { pin -> viewModel.checkPincode(pin) },
                                onAddToCart = { prod, qty, color, size ->
                                    viewModel.addToCart(prod, qty, color, size)
                                },
                                onBuyNow = { prod ->
                                    isCheckingOut = true
                                },
                                onToggleWishlist = { viewModel.toggleWishlist(selectedProduct) },
                                onToggleCompare = { viewModel.toggleCompare(selectedProduct.id) },
                                onSubmitReview = { rating, title, comment ->
                                    viewModel.submitReview(selectedProduct.id, rating, title, comment)
                                },
                                onBackClick = { selectedProductId = null },
                                onRelatedProductClick = { rel -> selectedProductId = rel.id },
                                isWideScreen = isWideScreen
                            )
                        }

                        // 2. Order Detail & Live Tracking Screen
                        selectedOrder != null -> {
                            OrderDetailScreen(
                                order = selectedOrder,
                                onAdvanceStatus = { viewModel.advanceOrderStatus(selectedOrder.id) },
                                onCancelOrder = { reason -> viewModel.cancelOrder(selectedOrder.id, reason) },
                                onReturnOrder = { reason -> viewModel.returnOrder(selectedOrder.id, reason) },
                                onBackClick = { selectedOrderId = null }
                            )
                        }

                        // 3. Checkout Screen
                        isCheckingOut -> {
                            CheckoutScreen(
                                cartItems = cartItems,
                                cartSummary = cartSummary,
                                appliedCoupon = appliedCoupon,
                                savedAddresses = savedAddresses,
                                selectedAddress = selectedAddress,
                                onSelectAddress = { addr -> viewModel.selectAddress(addr) },
                                onAddNewAddress = { addr -> viewModel.saveAddress(addr) },
                                onPlaceOrder = { payMethod, onDone ->
                                    viewModel.placeOrder(payMethod, onDone)
                                },
                                onTrackOrder = { orderId ->
                                    isCheckingOut = false
                                    selectedOrderId = orderId
                                },
                                onContinueShopping = {
                                    isCheckingOut = false
                                    currentDestination = NavDestination.HOME
                                },
                                onBackClick = { isCheckingOut = false },
                                isWideScreen = isWideScreen
                            )
                        }

                        // 4. Notifications Screen
                        isViewingNotifications -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                onNotificationClick = { notif ->
                                    viewModel.markNotificationRead(notif.id)
                                    if (notif.targetOrderId != null) {
                                        selectedOrderId = notif.targetOrderId
                                        isViewingNotifications = false
                                    } else if (notif.targetProductId != null) {
                                        selectedProductId = notif.targetProductId
                                        isViewingNotifications = false
                                    }
                                },
                                onBackClick = { isViewingNotifications = false }
                            )
                        }

                        // 5. Support & FAQ Screen
                        isViewingSupport -> {
                            SupportScreen(
                                tickets = supportTickets,
                                onCreateTicket = { orderId, sub, cat, msg ->
                                    viewModel.createSupportTicket(orderId, sub, cat, msg)
                                },
                                onBackClick = { isViewingSupport = false }
                            )
                        }

                        // 6. Compare Products Screen
                        isViewingCompare -> {
                            CompareScreen(
                                comparedProducts = comparedProducts,
                                onProductClick = { prod ->
                                    selectedProductId = prod.id
                                    isViewingCompare = false
                                },
                                onAddToCart = { prod -> viewModel.addToCart(prod) },
                                onRemoveFromCompare = { id -> viewModel.toggleCompare(id) },
                                onClearAll = { viewModel.clearCompare() },
                                onStartShopping = {
                                    isViewingCompare = false
                                    currentDestination = NavDestination.HOME
                                }
                            )
                        }

                        // 7. Wishlist Screen
                        isViewingWishlist -> {
                            WishlistScreen(
                                wishlistItems = detailedWishlistItems,
                                onProductClick = { prod ->
                                    selectedProductId = prod.id
                                    isViewingWishlist = false
                                },
                                onAddToCart = { prod -> viewModel.addToCart(prod) },
                                onMoveToCart = { prod -> viewModel.moveWishlistToCart(prod) },
                                onRemoveFromWishlist = { prod -> viewModel.toggleWishlist(prod) },
                                onUpdateNote = { prodId, note -> viewModel.updateWishlistNote(prodId, note) },
                                onTogglePriceDropAlert = { prodId, notify -> viewModel.togglePriceDropAlert(prodId, notify) },
                                onAddAllToCart = { viewModel.addAllWishlistToCart() },
                                onClearAll = { viewModel.clearWishlist() },
                                onSyncCloud = { viewModel.syncWishlistFromCloud() },
                                onStartShopping = {
                                    isViewingWishlist = false
                                    currentDestination = NavDestination.HOME
                                },
                                isWideScreen = isWideScreen
                            )
                        }

                        // 8. Main Tab Destinations
                        else -> {
                            when (currentDestination) {
                                NavDestination.HOME -> {
                                    HomeScreen(
                                        products = products,
                                        categories = categories,
                                        recentOrders = orders,
                                        wishlistProductIds = wishlistProductIds,
                                        onProductClick = { prod -> selectedProductId = prod.id },
                                        onCategoryClick = { catId ->
                                            viewModel.onSelectCategory(catId)
                                            currentDestination = NavDestination.CATEGORIES
                                        },
                                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                                        onTrackOrder = { ordId -> selectedOrderId = ordId },
                                        onViewAllClick = {
                                            viewModel.onSelectCategory("all")
                                            currentDestination = NavDestination.CATEGORIES
                                        },
                                        isWideScreen = isWideScreen
                                    )
                                }

                                NavDestination.CATEGORIES -> {
                                    CategoryExplorerScreen(
                                        categories = categories,
                                        selectedCategoryId = selectedCategoryId,
                                        onSelectCategory = { catId -> viewModel.onSelectCategory(catId) },
                                        products = products,
                                        wishlistProductIds = wishlistProductIds,
                                        onProductClick = { prod -> selectedProductId = prod.id },
                                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                                        isWideScreen = isWideScreen
                                    )
                                }

                                NavDestination.SEARCH -> {
                                    SearchScreen(
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { query -> viewModel.onSearchQueryChange(query) },
                                        onSearchSubmit = { query -> viewModel.submitSearch(query) },
                                        recentSearches = recentSearches,
                                        onClearRecentSearches = { viewModel.clearRecentSearches() },
                                        products = products,
                                        availableBrands = viewModel.getAvailableBrands(selectedCategoryId),
                                        filterCriteria = filterCriteria,
                                        sortOption = sortOption,
                                        onUpdateFilter = { f -> viewModel.onUpdateFilter(f) },
                                        onResetFilter = { viewModel.resetFilters() },
                                        onSelectSort = { s -> viewModel.onSelectSort(s) },
                                        wishlistProductIds = wishlistProductIds,
                                        onProductClick = { prod -> selectedProductId = prod.id },
                                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                                        isWideScreen = isWideScreen
                                    )
                                }

                                NavDestination.CART -> {
                                    CartScreen(
                                        cartItems = cartItems,
                                        cartSummary = cartSummary,
                                        appliedCoupon = appliedCoupon,
                                        availableCoupons = coupons,
                                        onUpdateQuantity = { id, q -> viewModel.updateCartQuantity(id, q) },
                                        onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                        onApplyCoupon = { coup -> viewModel.applyCoupon(coup) },
                                        onApplyCouponCode = { code -> viewModel.applyCouponCode(code) },
                                        onRemoveCoupon = { viewModel.removeCoupon() },
                                        onProceedToCheckout = { isCheckingOut = true },
                                        onStartShopping = { currentDestination = NavDestination.HOME },
                                        isWideScreen = isWideScreen
                                    )
                                }

                                NavDestination.ORDERS -> {
                                    OrdersScreen(
                                        orders = orders,
                                        onOrderClick = { ordId -> selectedOrderId = ordId },
                                        onStartShopping = { currentDestination = NavDestination.HOME }
                                    )
                                }

                                NavDestination.ACCOUNT -> {
                                    AccountScreen(
                                        user = currentUser,
                                        savedAddressesCount = savedAddresses.size,
                                        ordersCount = orders.size,
                                        notificationsCount = unreadNotificationCount,
                                        appLanguage = appLanguage,
                                        onNavigateOrders = { currentDestination = NavDestination.ORDERS },
                                        onNavigateAddresses = { showAddressSheet = true },
                                        onNavigateWishlist = { isViewingWishlist = true },
                                        onNavigateNotifications = { isViewingNotifications = true },
                                        onNavigateSupport = { isViewingSupport = true },
                                        onShowAuthDialog = { showAuthDialog = true },
                                        onSignOut = { viewModel.signOut() },
                                        onChangeLanguage = { lang -> viewModel.setLanguage(lang) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Address Selector Modal Sheet
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Select Delivery Location",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                savedAddresses.forEach { addr ->
                    val isSelected = selectedAddress?.id == addr.id
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.selectAddress(addr)
                                showAddressSheet = false
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectAddress(addr)
                                    showAddressSheet = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${addr.fullName} (${addr.label})", fontWeight = FontWeight.Bold)
                                Text(text = "${addr.line1}, ${addr.city} - ${addr.pincode}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthDialog(
            onGoogleSignIn = {
                viewModel.signInWithGoogle()
                showAuthDialog = false
            },
            onEmailSignIn = { email, pass ->
                viewModel.signInWithEmail(email, pass) { success ->
                    if (success) showAuthDialog = false
                }
            },
            onEmailSignUp = { name, email, pass ->
                viewModel.signUpWithEmail(name, email, pass) { success ->
                    if (success) showAuthDialog = false
                }
            },
            onContinueAsGuest = {
                viewModel.authHelper.continueAsGuest()
                showAuthDialog = false
            },
            onDismiss = { showAuthDialog = false }
        )
    }
}
