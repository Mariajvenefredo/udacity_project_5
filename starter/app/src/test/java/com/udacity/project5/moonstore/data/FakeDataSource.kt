package com.udacity.project5.moonstore.data

import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.data.local.Result
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem

class FakeDataSource(
    private val apiStoreItems: MutableList<StoreItemDTO>,
    private val apiStores: MutableList<PhysicalStoreDTO>,
    private val apiStoresWithStock: MutableList<PhysicalStoreDTO> = emptyList<PhysicalStoreDTO>().toMutableList(),
) : StoreDataSource {

    lateinit var storeItems: MutableList<StoreItemDTO>
    lateinit var stores: MutableList<PhysicalStoreDTO>

    var shouldReturnError = false
    var hasStock = false

    override suspend fun addStoreItemsToDatabase() {
        storeItems = apiStoreItems
    }

    override suspend fun addStoresToDatabase() {
        stores = apiStores
    }

    override suspend fun updateFavoriteStatus(storeItem: StoreItem) {
        val item = storeItems.firstOrNull { item ->
            item.id == storeItem.id
        }
        storeItems.remove(item)
        item?.markedAsFavorite = !item?.markedAsFavorite!!
        storeItems.add(item)
    }

    override suspend fun getStoreItems(storeItemFilter: StoreItemFilter): Result<List<StoreItem>> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        val dtoList = if (storeItemFilter == StoreItemFilter.FAVORITES) {
            storeItems.filter { item -> item.markedAsFavorite }
        } else {
            storeItems
        }
        val list = dtoList.map { item ->
            item.toStoreDataItem()
        }
        return Result.Success(list.toList())
    }

    override suspend fun getStoresWithStockForItem(storeItem: StoreItem): Result<List<Store>> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        return Result.Success(apiStoresWithStock.map { store -> store.toStore() })
    }

    override suspend fun getStores(): Result<List<Store>> {
        addStoresToDatabase()
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        return Result.Success(stores.map { store -> store.toStore() })
    }

    override suspend fun getStoreStockForItem(
        storeID: String,
        storeItemID: String
    ): Result<Boolean> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        return Result.Success(hasStock)
    }

    override suspend fun getStore(storeId: String): Result<Store> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        val store = stores.firstOrNull { store -> store.id.toString() == storeId }
        if (store != null) {
            return Result.Success(store.toStore())
        }
        return Result.Error("Exception")
    }
}