package com.udacity.moonstore.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "physicalStores")
class PhysicalStoreDTO(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "lat") var latitude: Double = 0.0,
    @ColumnInfo(name = "lng") var longitude: Double = 0.0,
)