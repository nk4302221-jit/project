package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.catalog.CatalogData
import com.example.data.local.*
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ShopWaveRepository(
    private val context: Context,
    private val db: ShopWaveDatabase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:365937247800:android:shopwave")
                    .setApiKey("AIzaSyDummyKeyForShopWaveBuildEnvSafe123")
                    .setProjectId("shopwave-ecom")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("ShopWaveRepository", "Firestore init note: ${e.message}")
            null
        }
    }

    private val _products = MutableStateFlow(CatalogData.products)
    val products: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow(CatalogData.categories)
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _coupons = MutableStateFlow(CatalogData.sampleCoupons)
    val coupons: StateFlow<List<Coupon>> = _coupons

    private val _reviews = MutableStateFlow(CatalogData.sampleReviews)
    val reviews: StateFlow<List<Review>> = _reviews

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets

    private val _comparedProductIds = MutableStateFlow<List<String>>(emptyList())
    val comparedProductIds: StateFlow<List<String>> = _comparedProductIds

    // Reactive Room Streams
    val cartItems: Flow<List<CartItem>> = db.cartDao().getAllCartItems().map { entities ->
        entities.mapNotNull { entity ->
            val prod = getProductById(entity.productId)
            if (prod != null) {
                CartItem(
                    product = prod,
                    quantity = entity.quantity,
                    selectedColor = entity.selectedColor,
                    selectedSize = entity.selectedSize,
                    addedAt = entity.addedAt
                )
            } else null
        }
    }

    val wishlistEntities: Flow<List<WishlistEntity>> = db.wishlistDao().getAllWishlist()

    val wishlistProductIds: Flow<Set<String>> = wishlistEntities.map { list ->
        list.map { it.productId }.toSet()
    }

    val wishlistItems: Flow<List<Product>> = wishlistEntities.map { list ->
        val idSet = list.map { it.productId }.toSet()
        CatalogData.products.filter { idSet.contains(it.id) }
    }

    val detailedWishlistItems: Flow<List<WishlistItem>> = wishlistEntities.map { list ->
        list.mapNotNull { entity ->
            val prod = getProductById(entity.productId)
            if (prod != null) {
                WishlistItem(
                    id = entity.productId,
                    userId = "",
                    product = prod,
                    addedAt = entity.addedAt,
                    note = entity.note,
                    notifyPriceDrop = entity.notifyPriceDrop
                )
            } else null
        }
    }

    val savedAddresses: Flow<List<Address>> = db.addressDao().getAllAddresses().map { list ->
        if (list.isEmpty()) {
            CatalogData.sampleAddresses
        } else {
            list.map { entity ->
                Address(
                    id = entity.id,
                    fullName = entity.fullName,
                    phone = entity.phone,
                    line1 = entity.line1,
                    line2 = entity.line2,
                    city = entity.city,
                    state = entity.state,
                    pincode = entity.pincode,
                    label = entity.label,
                    isDefault = entity.isDefault
                )
            }
        }
    }

    val recentSearches: Flow<List<String>> = db.recentSearchDao().getRecentSearches()

    val notifications: Flow<List<AppNotification>> = db.notificationDao().getAllNotifications().map { list ->
        if (list.isEmpty()) {
            listOf(
                AppNotification(
                    id = "notif_welcome",
                    title = "Welcome to ShopWave!",
                    message = "Enjoy up to 50% discount on electronics and free express delivery on your first order with coupon WELCOME50.",
                    type = "OFFER",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isRead = false
                ),
                AppNotification(
                    id = "notif_deal",
                    title = "⚡ Flash Deal Live",
                    message = "AuraWave Pro ANC Wireless Headphones are now 40% OFF for a limited time!",
                    type = "PRICE_DROP",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isRead = false,
                    targetProductId = "prod_tech_01"
                )
            )
        } else {
            list.map {
                AppNotification(
                    id = it.id,
                    title = it.title,
                    message = it.message,
                    type = it.type,
                    timestamp = it.timestamp,
                    isRead = it.isRead,
                    targetOrderId = it.targetOrderId,
                    targetProductId = it.targetProductId
                )
            }
        }
    }

    val orders: Flow<List<Order>> = db.orderDao().getAllOrders().map { entities ->
        entities.map { entity ->
            parseOrderEntity(entity)
        }
    }

    init {
        // Seed default addresses and notifications if table is empty
        coroutineScope.launch {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        try {
            for (addr in CatalogData.sampleAddresses) {
                db.addressDao().insert(
                    AddressEntity(
                        id = addr.id,
                        fullName = addr.fullName,
                        phone = addr.phone,
                        line1 = addr.line1,
                        line2 = addr.line2,
                        city = addr.city,
                        state = addr.state,
                        pincode = addr.pincode,
                        label = addr.label,
                        isDefault = addr.isDefault
                    )
                )
            }
            // Seed a sample order for immediate order tracking demo
            val sampleOrderItems = listOf(
                OrderItem(
                    productId = "prod_tech_01",
                    title = "AuraWave Pro ANC Wireless Headphones",
                    brand = "AuraSound",
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                    price = 14999.0,
                    quantity = 1,
                    variant = "Midnight Black"
                )
            )
            val demoOrder = Order(
                id = "order_demo_101",
                orderNumber = "SW-894210",
                userId = "user_demo",
                items = sampleOrderItems,
                address = CatalogData.sampleAddresses.first(),
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = "PAID (UPI Trans ID: 489271)",
                subtotal = 14999.0,
                discount = 1500.0,
                deliveryCharge = 0.0,
                tax = 2429.82,
                total = 15928.82,
                status = OrderStatus.SHIPPED,
                placedAtTimestamp = System.currentTimeMillis() - 86400000,
                estimatedDeliveryDate = getFutureDateFormatted(1),
                trackingSteps = generateTrackingSteps(OrderStatus.SHIPPED, System.currentTimeMillis() - 86400000),
                courierName = "BlueDart Air Express",
                trackingNumber = "BD-AIR-78291048"
            )
            db.orderDao().insertOrder(toOrderEntity(demoOrder))
        } catch (e: Exception) {
            Log.e("ShopWaveRepository", "Seed error: ${e.message}")
        }
    }

    // Product Queries
    fun getProductById(id: String): Product? {
        return CatalogData.products.find { it.id == id }
    }

    fun getProductsByCategory(categoryId: String): List<Product> {
        return CatalogData.products.filter { it.categoryId.equals(categoryId, ignoreCase = true) }
    }

    fun getProductsBySubcategory(subcategory: String): List<Product> {
        return CatalogData.products.filter { it.subcategory.equals(subcategory, ignoreCase = true) }
    }

    fun getRelatedProducts(product: Product): List<Product> {
        return CatalogData.products.filter { it.id != product.id && (it.categoryId == product.categoryId || it.subcategory == product.subcategory) }.take(4)
    }

    fun searchAndFilterProducts(
        query: String,
        categoryId: String? = null,
        filter: FilterCriteria = FilterCriteria(),
        sortOption: SortOption = SortOption.RELEVANCE
    ): List<Product> {
        var list = CatalogData.products

        if (categoryId != null && categoryId.isNotEmpty() && categoryId != "all") {
            list = list.filter { it.categoryId.equals(categoryId, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.subcategory.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) } ||
                it.description.lowercase().contains(q)
            }
        }

        // Apply filters
        list = list.filter { prod ->
            prod.price >= filter.minPrice &&
            prod.price <= filter.maxPrice &&
            (filter.selectedBrands.isEmpty() || filter.selectedBrands.contains(prod.brand)) &&
            prod.rating >= filter.minRating &&
            prod.discountPct >= filter.minDiscount &&
            (!filter.inStockOnly || prod.stock > 0) &&
            (!filter.primeDeliveryOnly || prod.isPrimeDelivery)
        }

        // Apply sort
        return when (sortOption) {
            SortOption.RELEVANCE -> list
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.RATING_HIGH -> list.sortedByDescending { it.rating }
            SortOption.DISCOUNT_HIGH -> list.sortedByDescending { it.discountPct }
            SortOption.NEWEST -> list.reversed()
        }
    }

    fun getAvailableBrands(categoryId: String? = null): List<String> {
        val prods = if (categoryId != null && categoryId != "all") {
            CatalogData.products.filter { it.categoryId.equals(categoryId, ignoreCase = true) }
        } else CatalogData.products
        return prods.map { it.brand }.distinct().sorted()
    }

    // Cart Actions
    suspend fun addToCart(product: Product, quantity: Int = 1, color: String? = null, size: String? = null) {
        db.cartDao().insertOrUpdate(
            CartItemEntity(
                productId = product.id,
                quantity = quantity,
                selectedColor = color,
                selectedSize = size
            )
        )
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().deleteByProductId(productId)
        } else {
            db.cartDao().updateQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) {
        db.cartDao().deleteByProductId(productId)
    }

    suspend fun clearCart() {
        db.cartDao().clearCart()
    }

    // Wishlist Actions
    suspend fun toggleWishlist(productId: String, user: UserProfile? = null, note: String = "") {
        val isIn = isProductInWishlist(productId)
        if (isIn) {
            removeFromWishlist(productId, user)
        } else {
            addToWishlist(productId, user, note)
        }
    }

    suspend fun addToWishlist(productId: String, user: UserProfile? = null, note: String = "") {
        val prod = getProductById(productId) ?: return
        db.wishlistDao().insert(
            WishlistEntity(
                productId = productId,
                addedAt = System.currentTimeMillis(),
                note = note,
                notifyPriceDrop = true
            )
        )
        syncWishlistItemToFirestore(prod, user, note, notifyPriceDrop = true)
    }

    suspend fun removeFromWishlist(productId: String, user: UserProfile? = null) {
        db.wishlistDao().delete(productId)
        deleteWishlistItemFromFirestore(productId, user)
    }

    suspend fun updateWishlistNote(productId: String, note: String, user: UserProfile? = null) {
        db.wishlistDao().updateNote(productId, note)
        val prod = getProductById(productId)
        if (prod != null) {
            val notify = isNotifyPriceDropEnabled(productId)
            syncWishlistItemToFirestore(prod, user, note, notifyPriceDrop = notify)
        }
    }

    suspend fun togglePriceDropAlert(productId: String, notify: Boolean, user: UserProfile? = null) {
        db.wishlistDao().updateNotifyPriceDrop(productId, notify)
        val prod = getProductById(productId)
        if (prod != null) {
            val note = getWishlistNote(productId)
            syncWishlistItemToFirestore(prod, user, note, notifyPriceDrop = notify)
        }
    }

    suspend fun clearWishlist(user: UserProfile? = null) {
        val current = db.wishlistDao().getAllWishlist().first()
        db.wishlistDao().clearWishlist()
        for (item in current) {
            deleteWishlistItemFromFirestore(item.productId, user)
        }
    }

    suspend fun isProductInWishlist(productId: String): Boolean {
        return wishlistProductIds.first().contains(productId)
    }

    private suspend fun getWishlistNote(productId: String): String {
        return try {
            db.wishlistDao().getAllWishlist().first().find { it.productId == productId }?.note ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private suspend fun isNotifyPriceDropEnabled(productId: String): Boolean {
        return try {
            db.wishlistDao().getAllWishlist().first().find { it.productId == productId }?.notifyPriceDrop ?: true
        } catch (e: Exception) {
            true
        }
    }

    // Compare Actions
    fun toggleCompare(productId: String) {
        val current = _comparedProductIds.value.toMutableList()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            if (current.size >= 4) {
                current.removeAt(0)
            }
            current.add(productId)
        }
        _comparedProductIds.value = current
    }

    fun clearCompare() {
        _comparedProductIds.value = emptyList()
    }

    // Search History
    suspend fun addRecentSearch(query: String) {
        if (query.isNotBlank()) {
            db.recentSearchDao().insert(RecentSearchEntity(query.trim()))
        }
    }

    suspend fun clearRecentSearches() {
        db.recentSearchDao().clearAll()
    }

    // Address Management
    suspend fun saveAddress(address: Address) {
        db.addressDao().insert(
            AddressEntity(
                id = address.id.ifEmpty { "addr_${System.currentTimeMillis()}" },
                fullName = address.fullName,
                phone = address.phone,
                line1 = address.line1,
                line2 = address.line2,
                city = address.city,
                state = address.state,
                pincode = address.pincode,
                label = address.label,
                isDefault = address.isDefault
            )
        )
        if (address.isDefault) {
            db.addressDao().setDefaultAddress(address.id)
        }
    }

    suspend fun deleteAddress(address: Address) {
        db.addressDao().delete(
            AddressEntity(
                id = address.id,
                fullName = address.fullName,
                phone = address.phone,
                line1 = address.line1,
                line2 = address.line2,
                city = address.city,
                state = address.state,
                pincode = address.pincode,
                label = address.label,
                isDefault = address.isDefault
            )
        )
    }

    // Orders & Tracking
    suspend fun placeOrder(
        user: UserProfile,
        cartItems: List<CartItem>,
        address: Address,
        paymentMethod: PaymentMethod,
        appliedCoupon: Coupon?
    ): Order {
        val orderItems = cartItems.map {
            OrderItem(
                productId = it.product.id,
                title = it.product.title,
                brand = it.product.brand,
                imageUrl = it.product.imageUrls.firstOrNull() ?: "",
                price = it.product.price,
                quantity = it.quantity,
                variant = listOfNotNull(it.selectedColor, it.selectedSize).joinToString(" / ")
            )
        }

        val subtotal = cartItems.sumOf { it.product.price * it.quantity }
        val discount = calculateDiscount(subtotal, appliedCoupon)
        val deliveryCharge = if (subtotal > 499 || appliedCoupon?.code == "FREESHIP") 0.0 else 99.0
        val taxableAmount = subtotal - discount
        val tax = taxableAmount * 0.18 // 18% GST standard e-comm
        val total = taxableAmount + deliveryCharge + tax

        val orderId = "order_${System.currentTimeMillis()}"
        val orderNum = "SW-${(100000..999999).random()}"
        val estDate = getFutureDateFormatted(2)

        val newOrder = Order(
            id = orderId,
            orderNumber = orderNum,
            userId = user.uid,
            items = orderItems,
            address = address,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == PaymentMethod.COD) "PENDING (COD)" else "PAID (Authorized)",
            subtotal = subtotal,
            discount = discount,
            deliveryCharge = deliveryCharge,
            tax = tax,
            total = total,
            status = OrderStatus.CONFIRMED,
            placedAtTimestamp = System.currentTimeMillis(),
            estimatedDeliveryDate = estDate,
            trackingSteps = generateTrackingSteps(OrderStatus.CONFIRMED, System.currentTimeMillis()),
            courierName = "BlueDart Express",
            trackingNumber = "SW-BD-${(1000000..9999999).random()}"
        )

        // Save in local Room
        db.orderDao().insertOrder(toOrderEntity(newOrder))
        // Clear Cart
        db.cartDao().clearCart()

        // Send order placement notification
        db.notificationDao().insert(
            NotificationEntity(
                id = "notif_order_${System.currentTimeMillis()}",
                title = "Order Confirmed! (#${newOrder.orderNumber})",
                message = "Your order of ₹${String.format(Locale.US, "%.2f", total)} is confirmed and being prepared for dispatch.",
                type = "ORDER",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                targetOrderId = newOrder.id,
                targetProductId = null
            )
        )

        // Sync with Firestore in background
        syncOrderWithFirestore(newOrder)

        return newOrder
    }

    suspend fun advanceOrderStatus(orderId: String): Order? {
        val entity = db.orderDao().getOrderById(orderId) ?: return null
        val currentOrder = parseOrderEntity(entity)
        val nextStatus = when (currentOrder.status) {
            OrderStatus.PLACED -> OrderStatus.CONFIRMED
            OrderStatus.CONFIRMED -> OrderStatus.PACKED
            OrderStatus.PACKED -> OrderStatus.SHIPPED
            OrderStatus.SHIPPED -> OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
            else -> currentOrder.status
        }
        if (nextStatus != currentOrder.status) {
            val updated = currentOrder.copy(
                status = nextStatus,
                trackingSteps = generateTrackingSteps(nextStatus, currentOrder.placedAtTimestamp)
            )
            db.orderDao().insertOrder(toOrderEntity(updated))

            // Add notification
            db.notificationDao().insert(
                NotificationEntity(
                    id = "notif_trk_${System.currentTimeMillis()}",
                    title = "Order Update: ${nextStatus.displayName}",
                    message = "Order #${currentOrder.orderNumber} is now ${nextStatus.displayName.lowercase()}.",
                    type = "ORDER",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    targetOrderId = orderId,
                    targetProductId = null
                )
            )
            syncOrderWithFirestore(updated)
            return updated
        }
        return currentOrder
    }

    suspend fun cancelOrder(orderId: String, reason: String) {
        db.orderDao().updateOrderStatus(orderId, OrderStatus.CANCELLED.name, reason)
        val entity = db.orderDao().getOrderById(orderId)
        if (entity != null) {
            val updated = parseOrderEntity(entity)
            syncOrderWithFirestore(updated)
        }
    }

    suspend fun returnOrder(orderId: String, reason: String) {
        db.orderDao().updateReturnStatus(orderId, OrderStatus.RETURNED.name, reason)
        val entity = db.orderDao().getOrderById(orderId)
        if (entity != null) {
            val updated = parseOrderEntity(entity)
            syncOrderWithFirestore(updated)
        }
    }

    // Reviews
    fun addReview(review: Review) {
        val current = _reviews.value.toMutableList()
        current.add(0, review)
        _reviews.value = current
    }

    // Support
    fun createSupportTicket(orderId: String?, subject: String, category: String, message: String): SupportTicket {
        val ticket = SupportTicket(
            id = "ticket_${System.currentTimeMillis()}",
            orderId = orderId,
            subject = subject,
            category = category,
            message = message,
            status = "In Progress",
            replies = listOf("Hi there! We've received your request. A ShopWave specialist is reviewing your inquiry and will update you shortly.")
        )
        val current = _supportTickets.value.toMutableList()
        current.add(0, ticket)
        _supportTickets.value = current
        return ticket
    }

    // Pincode SLA Checker
    fun checkDeliveryPincode(pincode: String): Pair<Boolean, String> {
        val trimmed = pincode.trim()
        if (trimmed.length != 6 || !trimmed.all { it.isDigit() }) {
            return Pair(false, "Please enter a valid 6-digit postal pincode")
        }
        return when (trimmed.first()) {
            '5', '6' -> Pair(true, "Express Delivery Available: Guaranteed Delivery by Tomorrow 8 PM")
            '1', '2' -> Pair(true, "Standard Priority Delivery: Arriving in 2 Business Days")
            '4' -> Pair(true, "Express Delivery Available: Delivery by Tomorrow Evening")
            else -> Pair(true, "Standard Delivery: Estimated Delivery within 2-3 Business Days")
        }
    }

    // Notifications
    suspend fun markNotificationAsRead(id: String) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        db.notificationDao().markAllAsRead()
    }

    // Calculations
    fun calculateDiscount(subtotal: Double, coupon: Coupon?): Double {
        if (coupon == null || subtotal < coupon.minOrderAmount) return 0.0
        return if (coupon.discountPct > 0) {
            val discount = subtotal * (coupon.discountPct / 100.0)
            minOf(discount, coupon.maxDiscount)
        } else {
            coupon.fixedDiscount
        }
    }

    private fun generateTrackingSteps(currentStatus: OrderStatus, placedAt: Long): List<TrackingStep> {
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val cal = Calendar.getInstance().apply { timeInMillis = placedAt }

        val steps = mutableListOf<TrackingStep>()
        val statuses = listOf(
            OrderStatus.PLACED to ("Order Placed" to "We have received your order."),
            OrderStatus.CONFIRMED to ("Order Confirmed" to "Seller has verified and accepted the order."),
            OrderStatus.PACKED to ("Packed & Sealed" to "Item packed with shockproof tamper seal."),
            OrderStatus.SHIPPED to ("In Transit" to "Handed over to BlueDart Courier facility."),
            OrderStatus.OUT_FOR_DELIVERY to ("Out for Delivery" to "Delivery executive is on the way to your doorstep."),
            OrderStatus.DELIVERED to ("Delivered" to "Package handed over safely.")
        )

        for ((idx, pair) in statuses.withIndex()) {
            val (status, meta) = pair
            val isCompleted = currentStatus.stepIndex >= status.stepIndex
            val isCurrent = currentStatus == status

            cal.add(Calendar.HOUR_OF_DAY, 4)
            val timeStr = if (isCompleted) formatter.format(cal.time) else "Expected"

            steps.add(
                TrackingStep(
                    status = status,
                    title = meta.first,
                    description = meta.second,
                    timestamp = timeStr,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent
                )
            )
        }
        return steps
    }

    private fun getFutureDateFormatted(daysInFuture: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, daysInFuture)
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // Entity Serializers
    private fun toOrderEntity(order: Order): OrderEntity {
        val itemsArray = JSONArray()
        for (item in order.items) {
            val obj = JSONObject().apply {
                put("productId", item.productId)
                put("title", item.title)
                put("brand", item.brand)
                put("imageUrl", item.imageUrl)
                put("price", item.price)
                put("quantity", item.quantity)
                put("variant", item.variant)
            }
            itemsArray.put(obj)
        }

        val addrObj = JSONObject().apply {
            put("id", order.address.id)
            put("fullName", order.address.fullName)
            put("phone", order.address.phone)
            put("line1", order.address.line1)
            put("line2", order.address.line2)
            put("city", order.address.city)
            put("state", order.address.state)
            put("pincode", order.address.pincode)
            put("label", order.address.label)
            put("isDefault", order.address.isDefault)
        }

        return OrderEntity(
            id = order.id,
            orderNumber = order.orderNumber,
            userId = order.userId,
            itemsJson = itemsArray.toString(),
            addressJson = addrObj.toString(),
            paymentMethodName = order.paymentMethod.name,
            paymentStatus = order.paymentStatus,
            subtotal = order.subtotal,
            discount = order.discount,
            deliveryCharge = order.deliveryCharge,
            tax = order.tax,
            total = order.total,
            statusName = order.status.name,
            placedAtTimestamp = order.placedAtTimestamp,
            estimatedDeliveryDate = order.estimatedDeliveryDate,
            courierName = order.courierName,
            trackingNumber = order.trackingNumber,
            cancellationReason = order.cancellationReason,
            returnReason = order.returnReason
        )
    }

    private fun parseOrderEntity(entity: OrderEntity): Order {
        val items = mutableListOf<OrderItem>()
        try {
            val array = JSONArray(entity.itemsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                items.add(
                    OrderItem(
                        productId = obj.optString("productId"),
                        title = obj.optString("title"),
                        brand = obj.optString("brand"),
                        imageUrl = obj.optString("imageUrl"),
                        price = obj.optDouble("price", 0.0),
                        quantity = obj.optInt("quantity", 1),
                        variant = obj.optString("variant", "")
                    )
                )
            }
        } catch (ignored: Exception) {}

        var address = CatalogData.sampleAddresses.first()
        try {
            val aObj = JSONObject(entity.addressJson)
            address = Address(
                id = aObj.optString("id"),
                fullName = aObj.optString("fullName"),
                phone = aObj.optString("phone"),
                line1 = aObj.optString("line1"),
                line2 = aObj.optString("line2"),
                city = aObj.optString("city"),
                state = aObj.optString("state"),
                pincode = aObj.optString("pincode"),
                label = aObj.optString("label", "Home"),
                isDefault = aObj.optBoolean("isDefault", false)
            )
        } catch (ignored: Exception) {}

        val status = try {
            OrderStatus.valueOf(entity.statusName)
        } catch (e: Exception) {
            OrderStatus.CONFIRMED
        }

        val payMethod = try {
            PaymentMethod.valueOf(entity.paymentMethodName)
        } catch (e: Exception) {
            PaymentMethod.UPI
        }

        return Order(
            id = entity.id,
            orderNumber = entity.orderNumber,
            userId = entity.userId,
            items = items,
            address = address,
            paymentMethod = payMethod,
            paymentStatus = entity.paymentStatus,
            subtotal = entity.subtotal,
            discount = entity.discount,
            deliveryCharge = entity.deliveryCharge,
            tax = entity.tax,
            total = entity.total,
            status = status,
            placedAtTimestamp = entity.placedAtTimestamp,
            estimatedDeliveryDate = entity.estimatedDeliveryDate,
            trackingSteps = generateTrackingSteps(status, entity.placedAtTimestamp),
            courierName = entity.courierName,
            trackingNumber = entity.trackingNumber,
            cancellationReason = entity.cancellationReason,
            returnReason = entity.returnReason
        )
    }

    private fun syncOrderWithFirestore(order: Order) {
        coroutineScope.launch {
            try {
                val orderMap = hashMapOf(
                    "id" to order.id,
                    "orderNumber" to order.orderNumber,
                    "userId" to order.userId,
                    "total" to order.total,
                    "status" to order.status.name,
                    "placedAt" to order.placedAtTimestamp,
                    "estimatedDelivery" to order.estimatedDeliveryDate,
                    "courier" to order.courierName,
                    "trackingNumber" to order.trackingNumber
                )
                firestore.collection("orders").document(order.id).set(orderMap, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w("ShopWaveRepository", "Firestore background sync: ${e.message}")
            }
        }
    }

    private fun syncWishlistItemToFirestore(
        product: Product,
        user: UserProfile?,
        note: String = "",
        notifyPriceDrop: Boolean = true
    ) {
        val uid = user?.uid ?: "guest_user"
        val docId = "${uid}_${product.id}"
        coroutineScope.launch {
            try {
                val data = hashMapOf(
                    "id" to docId,
                    "userId" to uid,
                    "userEmail" to (user?.email ?: ""),
                    "productId" to product.id,
                    "title" to product.title,
                    "brand" to product.brand,
                    "price" to product.price,
                    "originalPrice" to product.originalPrice,
                    "discountPct" to product.discountPct,
                    "imageUrl" to (product.imageUrls.firstOrNull() ?: ""),
                    "categoryId" to product.categoryId,
                    "rating" to product.rating,
                    "reviewCount" to product.reviewCount,
                    "inStock" to (product.stock > 0),
                    "stock" to product.stock,
                    "note" to note,
                    "notifyPriceDrop" to notifyPriceDrop,
                    "addedAt" to System.currentTimeMillis(),
                    "savedForFuturePurchase" to true
                )
                firestore.collection("wishlists")
                    .document(docId)
                    .set(data, SetOptions.merge())
                    .await()
                Log.d("ShopWaveRepository", "Synced wishlist item to Firestore 'wishlists': $docId")
            } catch (e: Exception) {
                Log.w("ShopWaveRepository", "Firestore wishlist sync failed: ${e.message}")
            }
        }
    }

    private fun deleteWishlistItemFromFirestore(productId: String, user: UserProfile?) {
        val uid = user?.uid ?: "guest_user"
        val docId = "${uid}_${productId}"
        coroutineScope.launch {
            try {
                firestore.collection("wishlists")
                    .document(docId)
                    .delete()
                    .await()
                Log.d("ShopWaveRepository", "Deleted wishlist item from Firestore 'wishlists': $docId")
            } catch (e: Exception) {
                Log.w("ShopWaveRepository", "Firestore wishlist delete failed: ${e.message}")
            }
        }
    }

    suspend fun syncWishlistFromFirestore(userId: String) {
        try {
            val snapshot = firestore.collection("wishlists")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                val entities = snapshot.documents.mapNotNull { doc ->
                    val prodId = doc.getString("productId") ?: return@mapNotNull null
                    val addedAt = doc.getLong("addedAt") ?: System.currentTimeMillis()
                    val note = doc.getString("note") ?: ""
                    val notify = doc.getBoolean("notifyPriceDrop") ?: true
                    WishlistEntity(
                        productId = prodId,
                        addedAt = addedAt,
                        note = note,
                        notifyPriceDrop = notify
                    )
                }
                if (entities.isNotEmpty()) {
                    db.wishlistDao().insertAll(entities)
                }
            }
        } catch (e: Exception) {
            Log.w("ShopWaveRepository", "Failed to fetch wishlist from Firestore: ${e.message}")
        }
    }
}
