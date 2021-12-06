package com.udacity.moonstore.storeItems

import android.os.Parcelable
import com.udacity.moonstore.data.dto.StoreItemDTO
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * data class acts as a data mapper between the DB and the UI
 */
@Parcelize
data class StoreItem(
    var id: Long,
    var name: String,
    var url: String,
    var price: Double,
    var markedAsFavorite: Boolean = false
) : Parcelable, Serializable{
    fun toStoreItemDTO(): StoreItemDTO {
        return StoreItemDTO(id, name, url, price, markedAsFavorite)
    }
}