package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.CatalogData
import com.example.data.firebase.FirebaseAuthHelper
import com.example.data.local.ShopWaveDatabase
import com.example.data.repository.ShopWaveRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShopWaveViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ShopWaveDatabase.getDatabase(application)
    val authHelper = FirebaseAuthHelper(application)
    val repository = ShopWaveRepository(application, db, viewModelScope)

    // User State
    val currentUser: StateFlow<UserProfile?> = authHelper.currentUser

    // Catalog & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategoryId = MutableStateFlow("all")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId

    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria

    private val _sortOption = MutableStateFlow(SortOption.RELEVANCE)
    val sortOption: StateFlow<SortOption> = _sortOption

    val categories: StateFlow<List<CategoryItem>> = repository.categories
    val coupons: StateFlow<List<Coupon>> = repository.coupons
    val reviews: StateFlow<List<Review>> = repository.reviews
    val supportTickets: StateFlow<List<SupportTicket>> = repository.supportTickets
    val comparedProductIds: StateFlow<List<String>> = repository.comparedProductIds

    // Reactive Filtered Products
    val productsList: StateFlow<List<Product>> = combine(
        _searchQuery,
        _selectedCategoryId,
        _filterCriteria,
        _sortOption
    ) { query, catId, filter, sort ->
        repository.searchAndFilterProducts(
            query = query,
            categoryId = catId,
            filter = filter,
            sortOption = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart & Checkout State
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val cartItemCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon

    val cartSummary = combine(cartItems, _appliedCoupon) { items, coupon ->
        val subtotal = items.sumOf { it.product.price * it.quantity }
        val discount = repository.calculateDiscount(subtotal, coupon)
        val delivery = if (subtotal > 499 || coupon?.code == "FREESHIP" || items.isEmpty()) 0.0 else 99.0
        val taxable = (subtotal - discount).coerceAtLeast(0.0)
        val tax = taxable * 0.18
        val total = if (items.isEmpty()) 0.0 else taxable + delivery + tax
        val freeDeliveryShortfall = (500.0 - subtotal).coerceAtLeast(0.0)

        CartSummaryState(
            subtotal = subtotal,
            discount = discount,
            deliveryCharge = delivery,
            tax = tax,
            total = total,
            freeDeliveryShortfall = freeDeliveryShortfall,
            eligibleForFreeDelivery = subtotal >= 499
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummaryState())

    // Wishlist
    val wishlistProductIds: StateFlow<Set<String>> = repository.wishlistProductIds.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    val wishlistItems: StateFlow<List<Product>> = repository.wishlistItems.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val detailedWishlistItems: StateFlow<List<WishlistItem>> = repository.detailedWishlistItems.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Addresses
    val savedAddresses: StateFlow<List<Address>> = repository.savedAddresses.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress

    // Orders & Tracking
    val orders: StateFlow<List<Order>> = repository.orders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Notifications
    val notifications: StateFlow<List<AppNotification>> = repository.notifications.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentSearches: StateFlow<List<String>> = repository.recentSearches.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Pincode SLA Result
    private val _pincodeResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val pincodeResult: StateFlow<Pair<Boolean, String>?> = _pincodeResult

    // UI Feedback Banner/Toast
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage

    // App Preferences
    private val _appLanguage = MutableStateFlow("en")
    val appLanguage: StateFlow<String> = _appLanguage

    init {
        // Auto select default address when available
        viewModelScope.launch {
            savedAddresses.collect { list ->
                if (_selectedAddress.value == null && list.isNotEmpty()) {
                    _selectedAddress.value = list.firstOrNull { it.isDefault } ?: list.first()
                }
            }
        }

        // Sync Firestore wishlist collection on user change
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    repository.syncWishlistFromFirestore(user.uid)
                }
            }
        }
    }

    // Actions
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun submitSearch(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.addRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    fun onSelectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun onUpdateFilter(filter: FilterCriteria) {
        _filterCriteria.value = filter
    }

    fun resetFilters() {
        _filterCriteria.value = FilterCriteria()
        _sortOption.value = SortOption.RELEVANCE
    }

    fun onSelectSort(sort: SortOption) {
        _sortOption.value = sort
    }

    fun addToCart(product: Product, quantity: Int = 1, color: String? = null, size: String? = null) {
        viewModelScope.launch {
            repository.addToCart(product, quantity, color, size)
            showUserMessage("Added \"${product.title}\" to Cart")
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showUserMessage("Item removed from cart")
        }
    }

    fun applyCoupon(coupon: Coupon) {
        val currentSubtotal = cartSummary.value.subtotal
        if (currentSubtotal < coupon.minOrderAmount) {
            showUserMessage("Minimum order amount of ₹${coupon.minOrderAmount.toInt()} required for this coupon")
            return
        }
        _appliedCoupon.value = coupon
        showUserMessage("Coupon ${coupon.code} applied successfully!")
    }

    fun applyCouponCode(code: String) {
        val match = repository.coupons.value.find { it.code.equals(code.trim(), ignoreCase = true) }
        if (match != null) {
            applyCoupon(match)
        } else {
            showUserMessage("Invalid coupon code. Try WAVE20 or WELCOME50")
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        showUserMessage("Coupon removed")
    }

    fun toggleWishlist(product: Product, note: String = "") {
        viewModelScope.launch {
            val user = currentUser.value
            repository.toggleWishlist(product.id, user, note)
            val isIn = repository.isProductInWishlist(product.id)
            showUserMessage(if (isIn) "Saved to Wishlist (Firestore synced)" else "Removed from Wishlist")
        }
    }

    fun updateWishlistNote(productId: String, note: String) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.updateWishlistNote(productId, note, user)
            showUserMessage("Wishlist note updated")
        }
    }

    fun togglePriceDropAlert(productId: String, notify: Boolean) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.togglePriceDropAlert(productId, notify, user)
            showUserMessage(if (notify) "Price drop alerts enabled" else "Price drop alerts turned off")
        }
    }

    fun moveWishlistToCart(product: Product, color: String? = null, size: String? = null) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.addToCart(product, quantity = 1, color = color, size = size)
            repository.removeFromWishlist(product.id, user)
            showUserMessage("Moved \"${product.title}\" to Cart")
        }
    }

    fun addAllWishlistToCart() {
        viewModelScope.launch {
            val items = detailedWishlistItems.value
            if (items.isEmpty()) return@launch
            var addedCount = 0
            for (item in items) {
                if (item.product.stock > 0) {
                    repository.addToCart(item.product, quantity = 1)
                    addedCount++
                }
            }
            showUserMessage("Added $addedCount items from Wishlist to Cart")
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            val user = currentUser.value
            repository.clearWishlist(user)
            showUserMessage("Wishlist cleared")
        }
    }

    fun syncWishlistFromCloud() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                repository.syncWishlistFromFirestore(user.uid)
                showUserMessage("Wishlist synced with Firestore Cloud")
            }
        }
    }

    fun toggleCompare(productId: String) {
        repository.toggleCompare(productId)
    }

    fun clearCompare() {
        repository.clearCompare()
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            repository.saveAddress(address)
            _selectedAddress.value = address
            showUserMessage("Delivery address saved!")
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            repository.deleteAddress(address)
            showUserMessage("Address deleted")
        }
    }

    fun checkPincode(pincode: String) {
        _pincodeResult.value = repository.checkDeliveryPincode(pincode)
    }

    fun placeOrder(paymentMethod: PaymentMethod, onPlaced: (Order) -> Unit) {
        val user = currentUser.value ?: UserProfile("guest", "Customer", "")
        val items = cartItems.value
        val addr = selectedAddress.value ?: CatalogData.sampleAddresses.first()

        if (items.isEmpty()) {
            showUserMessage("Your cart is empty!")
            return
        }

        viewModelScope.launch {
            val order = repository.placeOrder(
                user = user,
                cartItems = items,
                address = addr,
                paymentMethod = paymentMethod,
                appliedCoupon = appliedCoupon.value
            )
            _appliedCoupon.value = null
            showUserMessage("Order #${order.orderNumber} placed successfully!")
            onPlaced(order)
        }
    }

    fun advanceOrderStatus(orderId: String) {
        viewModelScope.launch {
            val updated = repository.advanceOrderStatus(orderId)
            if (updated != null) {
                showUserMessage("Order updated: ${updated.status.displayName}")
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.cancelOrder(orderId, reason)
            showUserMessage("Order cancelled successfully.")
        }
    }

    fun returnOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.returnOrder(orderId, reason)
            showUserMessage("Return request initiated.")
        }
    }

    fun submitReview(productId: String, rating: Float, title: String, comment: String) {
        val user = currentUser.value
        val review = Review(
            id = "rev_${System.currentTimeMillis()}",
            productId = productId,
            userName = user?.name ?: "Verified Shopper",
            rating = rating,
            title = title,
            comment = comment,
            date = "Just now",
            isVerifiedPurchase = true,
            helpfulCount = 0
        )
        repository.addReview(review)
        showUserMessage("Review submitted! Thank you for your feedback.")
    }

    fun createSupportTicket(orderId: String?, subject: String, category: String, message: String) {
        repository.createSupportTicket(orderId, subject, category, message)
        showUserMessage("Support ticket created. Our team will respond shortly.")
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showUserMessage("All notifications marked as read")
        }
    }

    fun signInWithGoogle(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = authHelper.signInWithGoogle()
            if (result.isSuccess) {
                showUserMessage("Welcome, ${result.getOrNull()?.name}!")
                onComplete(true)
            } else {
                showUserMessage("Google Sign-In failed")
                onComplete(false)
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authHelper.signInWithEmail(email, pass)
            if (result.isSuccess) {
                showUserMessage("Welcome back, ${result.getOrNull()?.name}!")
                onComplete(true)
            } else {
                showUserMessage("Login failed. Check credentials.")
                onComplete(false)
            }
        }
    }

    fun signUpWithEmail(name: String, email: String, pass: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authHelper.signUpWithEmail(name, email, pass)
            if (result.isSuccess) {
                showUserMessage("Account created successfully!")
                onComplete(true)
            } else {
                showUserMessage("Signup failed")
                onComplete(false)
            }
        }
    }

    fun signOut() {
        authHelper.signOut()
        showUserMessage("Signed out successfully")
    }

    fun setLanguage(lang: String) {
        _appLanguage.value = lang
        showUserMessage(if (lang == "hi") "भाषा बदलकर हिंदी कर दी गई है" else "Language changed to English")
    }

    fun showUserMessage(message: String) {
        _userMessage.value = message
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun getProductById(id: String): Product? = repository.getProductById(id)
    fun getRelatedProducts(product: Product): List<Product> = repository.getRelatedProducts(product)
    fun getAvailableBrands(categoryId: String?): List<String> = repository.getAvailableBrands(categoryId)
}

data class CartSummaryState(
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val freeDeliveryShortfall: Double = 0.0,
    val eligibleForFreeDelivery: Boolean = false
)
