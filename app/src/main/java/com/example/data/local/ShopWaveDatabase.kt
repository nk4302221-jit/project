package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        WishlistEntity::class,
        AddressEntity::class,
        RecentSearchEntity::class,
        NotificationEntity::class,
        OrderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ShopWaveDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun notificationDao(): NotificationDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: ShopWaveDatabase? = null

        fun getDatabase(context: Context): ShopWaveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopWaveDatabase::class.java,
                    "shopwave_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
