package com.udacity.moonstore.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.moonstore.storeItems.StoreItem

@Entity(tableName = "storeItems")
class StoreItemDTO(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "url") var url: String = "",
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "marked_as_favorite") var markedAsFavorite: Boolean = false,
    @ColumnInfo(name = "stock_notifications") val stockNotification: Boolean = false
) {
    fun toStoreDataItem(): StoreItem =
        StoreItem(id, name, url, price, markedAsFavorite)
}