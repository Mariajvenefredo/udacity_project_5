package com.udacity.moonstore.api.models

import android.os.Parcelable
import com.udacity.moonstore.data.dto.StoreItemDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreItem(
    val id: Long,
    val name: String,
    val url: String,
    val price: Double
) : Parcelable {
    fun toStoreItemDTO(): StoreItemDTO {
        return StoreItemDTO(id, name, url, price)
    }
}