package com.udacity.moonstore.data.local

import com.udacity.moonstore.api.API_KEY
import com.udacity.moonstore.api.StoreApi
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.dao.OnlineStoreDao
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
            val storedItemsList = onlineStoreDao.getFavoriteItems().toMutableList()
            val storeOnlineItems =
                StoreApi.retrofitService.getPieces(API_KEY)
                    .map { piece ->
                        piece.toStoreItemDTO()
                    }
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
            val stores = StoreApi.retrofitService.getStores(API_KEY)
                .map { store ->
                    store.toPhysicalStoreDTO()
                }

            onlineStoreDao.insertStores(*stores.toTypedArray())
        }

    override suspend fun getStoreItems(storeItemFilter: StoreItemFilter): List<StoreItem> =
        withContext(ioDispatcher) {
            if (storeItemFilter == StoreItemFilter.ALL) {
                onlineStoreDao.getStoreItems().map { storeItemDTO ->
                    storeItemDTO.toStoreDataItem()
                }
            } else {
                onlineStoreDao.getFavoriteItems().map { storeItemDTO ->
                    storeItemDTO.toStoreDataItem()
                }
            }
        }

    override suspend fun updateFavoriteStatus(storeItem: StoreItem) =
        withContext(ioDispatcher)
        {
            onlineStoreDao.insertStoreItem(storeItem.toStoreItemDTO())
        }

    override suspend fun getStoresWithStockForItem(storeItem: StoreItem): List<Store> =
        withContext(ioDispatcher)
        {
            StoreApi.retrofitService.getPieceStock(storeItem.id.toString(), API_KEY)
        }

    override suspend fun getStoreStockForItem(storeID: String, storeItemID: String): Boolean =
        withContext(ioDispatcher)
        {
            val result =
                StoreApi.retrofitService.getStoreStockForPiece(storeID, storeItemID, API_KEY)
            if (result == "true") {
                return@withContext true
            }
            return@withContext false
        }

    override suspend fun getStore(storeId: String): Store =
        withContext(ioDispatcher)
        {
            onlineStoreDao.getStore(storeId.toLong()).toStore()
        }

    override suspend fun getStores(): List<Store> =
        withContext(ioDispatcher)
        {
            onlineStoreDao.getPhysicalStores().map { physicalStoreDTO ->
                physicalStoreDTO.toStore()
            }
        }

/*    suspend fun addAsteroidsToDatabase() {
        withContext(Dispatchers.IO) {
            val asteroidApiList = AsteroidApi.retrofitService.getAsteroids(Constants.API_KEY)
            val asteroids = parseAsteroidsStringResult(asteroidApiList)
            database.asteroidDao.insertAllAsteroids(*asteroids.asDatabaseAsteroids())
        }
    }

    *
    * Insert a reminder in the db.
    * @param favoriteItem the reminder to be inserted

    override suspend fun saveReminder(favoriteItem: FavoriteItemDTO): Long =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                favoriteItemsDao.saveFavoriteItem(favoriteItem)
            }
        }

    *
    * Get a reminder by its id
    * @param id to be used to get the reminder
    * @ return Result the holds a Success
    object with the Reminder or an Error
    object with the error message

    override suspend fun getReminder(id: String): Result<FavoriteItemDTO> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    val reminder = favoriteItemsDao.getFavoriteItemById(id)
                    if (reminder != null) {
                        return@withContext Result.Success(reminder)
                    } else {
                        return@withContext Result.Error("Reminder not found!")
                    }
                } catch (e: Exception) {
                    return@withContext Result.Error(e.localizedMessage)
                }
            }
        }

    *
    * Deletes all the reminders in the db

    override suspend fun deleteAllReminders() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                favoriteItemsDao.deleteAllFavorites()
            }
        }
    }*/


}
