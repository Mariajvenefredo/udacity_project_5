package com.udacity.moonstore.data

import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem

/**
 * Main entry point for accessing reminders data.
 */
interface StoreDataSource {
    suspend fun addStoreItemsToDatabase()
    suspend fun addStoresToDatabase()
    suspend fun getStoreItems(storeItemFilter: StoreItemFilter): List<StoreItem>
    suspend fun updateFavoriteStatus(storeItem: StoreItem)
    suspend fun getStoresWithStockForItem(storeItem: StoreItem): List<Store>
    suspend fun getStores(): List<Store>
    suspend fun getStoreStockForItem(storeID: String, storeItemID: String): Boolean
    suspend fun getStore(storeId: String): Store
}
