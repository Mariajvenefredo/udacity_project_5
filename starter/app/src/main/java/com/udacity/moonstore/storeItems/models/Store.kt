package com.udacity.moonstore.storeItems.models

import android.os.Parcelable
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Store(
    val id: Long,
    val name: String,
    val lat: Double,
    val lng: Double
) : Parcelable, Serializable {
    fun toPhysicalStoreDTO(): PhysicalStoreDTO {
        return PhysicalStoreDTO(id, name, lat, lng)
    }
}