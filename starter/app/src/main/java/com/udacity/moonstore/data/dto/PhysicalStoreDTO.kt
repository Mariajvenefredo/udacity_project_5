package com.udacity.moonstore.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.moonstore.api.models.Store

@Entity(tableName = "physicalStores")
class PhysicalStoreDTO(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "lat") var latitude: Double = 0.0,
    @ColumnInfo(name = "lng") var longitude: Double = 0.0,
){
    fun toStore(): Store =
        Store(id, name, latitude, longitude)
}