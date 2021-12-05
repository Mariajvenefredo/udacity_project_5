package com.udacity.moonstore.data

import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.storeItems.StoreDataItem

/**
 * Main entry point for accessing reminders data.
 */
interface StoreDataSource {
    suspend fun addStoreInformationToDatabase()
    suspend fun getStoreItems(storeItemFilter: StoreItemFilter): List<StoreDataItem>
    suspend fun updateFavoriteStatus(storeItem: StoreDataItem)
}
