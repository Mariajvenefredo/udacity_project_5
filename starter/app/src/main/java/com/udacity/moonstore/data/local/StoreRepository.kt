package com.udacity.moonstore.data.local

import com.udacity.moonstore.api.API_KEY
import com.udacity.moonstore.api.StoreApi
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.dao.OnlineStoreDao
import com.udacity.moonstore.storeItems.StoreDataItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreRepository(
    private val onlineStoreDao: OnlineStoreDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StoreDataSource {

    override suspend fun addStoreInformationToDatabase() =
        withContext(ioDispatcher) {
            val storedItemsList = onlineStoreDao.getFavoriteItems().toMutableList()
            val storeOnlineItems =
                StoreApi.retrofitService.getPieces(API_KEY)
                    .map { piece ->
                        piece.toStoreItemDTO()
                    }
            storeOnlineItems.forEach { onlineItem ->
                val isFavorite =
                    storedItemsList.firstOrNull { localItem -> localItem.id == onlineItem.id }
                if (isFavorite != null) {
                    onlineItem.markedAsFavorite = true
                }
            }
            onlineStoreDao.insertStoreItems(*storeOnlineItems.toTypedArray())
        }

    override suspend fun getStoreItems(storeItemFilter: StoreItemFilter): List<StoreDataItem> =
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

    override suspend fun updateFavoriteStatus(storeItem: StoreDataItem) =
        withContext(ioDispatcher)
        {
            onlineStoreDao.insertStoreItem(storeItem.toStoreItemDTO())
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
