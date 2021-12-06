package com.udacity.moonstore.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.moonstore.api.models.Store
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO

@Dao
interface OnlineStoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoreItems(vararg storeItems: StoreItemDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStores(vararg physicalStores: PhysicalStoreDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoreItem(storeItems: StoreItemDTO)

    @Query("SELECT * FROM physicalStores WHERE id == :id")
    suspend fun getStore(id: Long ): PhysicalStoreDTO

    @Query("SELECT * FROM storeItems")
    suspend fun getStoreItems(): List<StoreItemDTO>

    @Query("SELECT * FROM storeItems WHERE marked_as_favorite == :favorite")
    suspend fun getFavoriteItems(favorite: Boolean = true): List<StoreItemDTO>

    @Query("SELECT * FROM physicalStores")
    suspend fun getPhysicalStores(): List<PhysicalStoreDTO>

    @Query("DELETE FROM storeItems WHERE id == :storeItemId")
    fun deleteStoreItemFromLocal(storeItemId: Long)
}
