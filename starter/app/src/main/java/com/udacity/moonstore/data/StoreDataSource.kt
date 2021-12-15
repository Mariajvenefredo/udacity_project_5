package com.udacity.moonstore.data

import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.moonstore.data.local.Result

interface StoreDataSource {
    suspend fun addStoreItemsToDatabase()
    suspend fun addStoresToDatabase()
    suspend fun updateFavoriteStatus(storeItem: StoreItem)
    suspend fun getStoreItems(storeItemFilter: StoreItemFilter = StoreItemFilter.ALL): Result<List<StoreItem>>
    suspend fun getStoresWithStockForItem(storeItem: StoreItem): Result<List<Store>>
    suspend fun getStores(): Result<List<Store>>
    suspend fun getStoreStockForItem(storeID: String, storeItemID: String): Result<Boolean>
    suspend fun getStore(storeId: String): Result<Store>
}
