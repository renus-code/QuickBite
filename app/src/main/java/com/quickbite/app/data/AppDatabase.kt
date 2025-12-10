package com.quickbite.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quickbite.app.model.Order
import com.quickbite.app.model.User
import com.quickbite.app.model.GiftCard

@Database(entities = [User::class, Order::class, GiftCard::class], version = 4, exportSchema = false) // Bumped version to 4
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun giftCardDao(): GiftCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quickbite_database"
                )
                .fallbackToDestructiveMigration() // Added fallback migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
