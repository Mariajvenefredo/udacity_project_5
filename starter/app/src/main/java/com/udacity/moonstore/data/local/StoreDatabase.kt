package com.udacity.moonstore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.moonstore.data.dao.OnlineStoreDao
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO

@Database(
    entities = [StoreItemDTO::class, PhysicalStoreDTO::class],
    version = 1,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {

    companion object {

        private lateinit var database: StoreDatabase

        fun getInstance(context: Context): StoreDatabase {
            synchronized(StoreDatabase::class.java) {
                if (!Companion::database.isInitialized) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        StoreDatabase::class.java,
                        "favorites_db"
                    ).build()
                }
            }
            return database
        }
    }

    abstract fun onlineStoreDao(): OnlineStoreDao

}