package com.example.model

enum class OrderStatus(val displayName: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    CONFIRMED("Order Confirmed", 1),
    PACKED("Item Packed", 2),
    SHIPPED("Shipped", 3),
    OUT_FOR_DELIVERY("Out for Delivery", 4),
    DELIVERED("Delivered", 5),
    CANCELLED("Cancelled", -1),
    RETURNED("Returned", -2)
}

enum class PaymentMethod(val title: String, val subtitle: String) {
    UPI("UPI / QR Code", "Google Pay, PhonePe, Paytm, BHIM"),
    CARD("Credit / Debit Card", "Visa, MasterCard, RuPay"),
    NET_BANKING("Net Banking", "All major Indian banks"),
    WALLET("ShopWave Wallet / Amazon Pay", "Instant 1-click checkout"),
    COD("Cash on Delivery", "Pay via Cash / UPI at doorstep")
}

data class Product(
    val id: String,
    val title: String,
    val brand: String,
    val categoryId: String,
    val subcategory: String,
    val price: Double,
    val originalPrice: Double,
    val discountPct: Int,
    val rating: Float,
    val reviewCount: Int,
    val stock: Int,
    val imageUrls: List<String>,
    val description: String,
    val highlights: List<String>,
    val specifications: Map<String, String>,
    val isPrimeDelivery: Boolean = true,
    val deliveryDays: Int = 2,
    val isBestSeller: Boolean = false,
    val isDealOfTheDay: Boolean = false,
    val tags: List<String> = emptyList()
)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String,
    val bannerText: String,
    val subcategories: List<String>
)

data class CartItem(
    val product: Product,
    val quantity: Int,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

data class WishlistItem(
    val id: String = "",
    val userId: String = "",
    val product: Product,
    val addedAt: Long = System.currentTimeMillis(),
    val note: String = "",
    val notifyPriceDrop: Boolean = true
)

data class Address(
    val id: String,
    val fullName: String,
    val phone: String,
    val line1: String,
    val line2: String = "",
    val city: String,
    val state: String,
    val pincode: String,
    val label: String = "Home", // Home, Work, Other
    val isDefault: Boolean = false
)

data class OrderItem(
    val productId: String,
    val title: String,
    val brand: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val variant: String = ""
)

data class TrackingStep(
    val status: OrderStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class Order(
    val id: String,
    val orderNumber: String,
    val userId: String,
    val items: List<OrderItem>,
    val address: Address,
    val paymentMethod: PaymentMethod,
    val paymentStatus: String = "PAID",
    val subtotal: Double,
    val discount: Double,
    val deliveryCharge: Double,
    val tax: Double,
    val total: Double,
    val status: OrderStatus,
    val placedAtTimestamp: Long,
    val estimatedDeliveryDate: String,
    val trackingSteps: List<TrackingStep>,
    val courierName: String = "BlueDart Express",
    val trackingNumber: String = "SW-TRK-${System.currentTimeMillis() % 1000000}",
    val cancellationReason: String? = null,
    val returnReason: String? = null
)

data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val userAvatar: String = "",
    val rating: Float,
    val title: String,
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean = true,
    val helpfulCount: Int = 0
)

data class Coupon(
    val code: String,
    val title: String,
    val discountPct: Int = 0,
    val fixedDiscount: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDiscount: Double = 500.0,
    val description: String,
    val expiryDate: String = "Valid till month end"
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // ORDER, OFFER, PRICE_DROP, SYSTEM
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetOrderId: String? = null,
    val targetProductId: String? = null
)

data class SupportTicket(
    val id: String,
    val orderId: String?,
    val subject: String,
    val category: String,
    val message: String,
    val status: String = "Open",
    val createdAt: Long = System.currentTimeMillis(),
    val replies: List<String> = emptyList()
)

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val photoUrl: String = "",
    val isGuest: Boolean = false
)

enum class SortOption(val displayName: String) {
    RELEVANCE("Featured / Relevance"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING_HIGH("Customer Rating (4★+)"),
    DISCOUNT_HIGH("Biggest Discounts"),
    NEWEST("New Arrivals")
}

data class FilterCriteria(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 200000.0,
    val selectedBrands: Set<String> = emptySet(),
    val minRating: Float = 0f,
    val minDiscount: Int = 0,
    val inStockOnly: Boolean = false,
    val primeDeliveryOnly: Boolean = false
)
