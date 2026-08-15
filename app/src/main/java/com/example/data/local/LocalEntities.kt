package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis(),
    val note: String = "",
    val notifyPriceDrop: Boolean = true
)

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WishlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WishlistEntity>)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isInWishlist(productId: String): Flow<Boolean>

    @Query("UPDATE wishlist_items SET note = :note WHERE productId = :productId")
    suspend fun updateNote(productId: String, note: String)

    @Query("UPDATE wishlist_items SET notifyPriceDrop = :notify WHERE productId = :productId")
    suspend fun updateNotifyPriceDrop(productId: String, notify: Boolean)

    @Query("DELETE FROM wishlist_items")
    suspend fun clearWishlist()
}

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val line1: String,
    val line2: String,
    val city: String,
    val state: String,
    val pincode: String,
    val label: String,
    val isDefault: Boolean
)

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id ASC")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: AddressEntity)

    @Delete
    suspend fun delete(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = (CASE WHEN id = :selectedId THEN 1 ELSE 0 END)")
    suspend fun setDefaultAddress(selectedId: String)
}

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface RecentSearchDao {
    @Query("SELECT query FROM recent_searches ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val targetOrderId: String?,
    val targetProductId: String?
)

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: String)
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val userId: String,
    val itemsJson: String, // serialized OrderItems
    val addressJson: String, // serialized Address
    val paymentMethodName: String,
    val paymentStatus: String,
    val subtotal: Double,
    val discount: Double,
    val deliveryCharge: Double,
    val tax: Double,
    val total: Double,
    val statusName: String,
    val placedAtTimestamp: Long,
    val estimatedDeliveryDate: String,
    val courierName: String,
    val trackingNumber: String,
    val cancellationReason: String?,
    val returnReason: String?
)

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY placedAtTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET statusName = :status, cancellationReason = :reason WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, reason: String? = null)

    @Query("UPDATE orders SET statusName = :status, returnReason = :reason WHERE id = :orderId")
    suspend fun updateReturnStatus(orderId: String, status: String, reason: String? = null)
}
