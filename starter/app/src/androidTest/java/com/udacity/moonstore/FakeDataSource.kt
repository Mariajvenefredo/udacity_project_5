package com.udacity.moonstore

import com.udacity.project5.moonstore.data.ReminderDataSource

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var favoriteItems: MutableList<FavoriteItemDTO>) : ReminderDataSource {

    var shouldReturnError = false

    override suspend fun getReminders(): Result<List<FavoriteItemDTO>> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        return Result.Success(favoriteItems)
    }

    override suspend fun saveReminder(favoriteItem: FavoriteItemDTO): Long {
        if (shouldReturnError) {
            return 0
        }
        favoriteItems.add(favoriteItem)
        return 1
    }

    override suspend fun getReminder(id: String): Result<FavoriteItemDTO> {
        if (shouldReturnError) {
            return Result.Error("Exception")
        }
        val reminder = favoriteItems.firstOrNull { r -> r.id == id }

        return if (reminder != null) {
            Result.Success(reminder)
        } else {
            Result.Error("Exception")
        }
    }

    override suspend fun deleteAllReminders() {
        favoriteItems.clear()
    }
}