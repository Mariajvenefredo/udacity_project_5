package com.udacity.moonstore.data.local

import com.udacity.moonstore.api.API_KEY
import com.udacity.moonstore.api.StoreApi
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.dao.OnlineStoreDao
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreRepository(
    private val onlineStoreDao: OnlineStoreDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StoreDataSource {

    override suspend fun addStoreItemsToDatabase() =
        withContext(ioDispatcher) {
            val storedItemsList = onlineStoreDao.getFavoriteItems()!!.toMutableList()
            val storeOnlineItems = getItemsAPI()
            //this step is done so that we do not lose favorite items
            storeOnlineItems.forEach { onlineItem ->
                val isFavorite =
                    storedItemsList.firstOrNull { localItem -> localItem.id == onlineItem.id }
                if (isFavorite != null) {
                    onlineItem.markedAsFavorite = true
                }
            }

            onlineStoreDao.insertStoreItems(*storeOnlineItems.toTypedArray())
        }

    override suspend fun addStoresToDatabase() =
        withContext(ioDispatcher) {
            val stores = getStoresAPI()

            onlineStoreDao.insertStores(*stores.toTypedArray())
        }

    override suspend fun getStoreItems(storeItemFilter: StoreItemFilter): Result<List<StoreItem>> =
        withContext(ioDispatcher) {
            if (storeItemFilter == StoreItemFilter.ALL) {
                return@withContext try {
                    Result.Success(onlineStoreDao.getStoreItems()!!.map { storeItemDTO ->
                        storeItemDTO.toStoreDataItem()
                    })
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
            } else {
                return@withContext try {
                    Result.Success(onlineStoreDao.getFavoriteItems()!!.map { storeItemDTO ->
                        storeItemDTO.toStoreDataItem()
                    })
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)
                }
            }
        }

    override suspend fun updateFavoriteStatus(storeItem: StoreItem) =
        withContext(ioDispatcher)
        {
            onlineStoreDao.insertStoreItem(storeItem.toStoreItemDTO())
        }

    override suspend fun getStoresWithStockForItem(storeItem: StoreItem): Result<List<Store>> =
        withContext(ioDispatcher)
        {
            return@withContext try {

                Result.Success(
                    getStoresWithStockAPI(storeItem)
                )
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }

        }

    override suspend fun getStoreStockForItem(
        storeID: String,
        storeItemID: String
    ): Result<Boolean> =
        withContext(ioDispatcher)
        {
            try {
                val result = getStockForPieceAPI(storeID, storeItemID)
                if (result == "true") {
                    return@withContext Result.Success(true)
                }
                return@withContext Result.Success(false)
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }

    override suspend fun getStore(storeId: String): Result<Store> =
        withContext(ioDispatcher)
        {
            try {
                val store = onlineStoreDao.getStore(storeId.toLong())
                if (store != null) {
                    Result.Success(store.toStore())
                } else {
                    Result.Error("Store not found")
                }
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }

    override suspend fun getStores(): Result<List<Store>> =
        withContext(ioDispatcher)
        {
            return@withContext try {
                Result.Success(onlineStoreDao.getPhysicalStores().map { physicalStoreDTO ->
                    physicalStoreDTO.toStore()
                })
            } catch (ex: java.lang.Exception) {
                Result.Error(ex.localizedMessage)

            }
        }

    suspend fun getItemsAPI(): List<StoreItemDTO> = StoreApi.retrofitService.getPieces(API_KEY)
        .map { piece ->
            piece.toStoreItemDTO()
        }

    suspend fun getStoresAPI() = StoreApi.retrofitService.getStores(API_KEY)
        .map { store ->
            store.toPhysicalStoreDTO()
        }

    suspend fun getStockForPieceAPI(
        storeID: String,
        storeItemID: String
    ) = StoreApi.retrofitService.getStoreStockForPiece(storeID, storeItemID, API_KEY)

    suspend fun getStoresWithStockAPI(storeItem: StoreItem) =
        StoreApi.retrofitService.getPieceStock(
            storeItem.id.toString(),
            API_KEY
        )
}
