package com.udacity.moonstore.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double
) : Parcelable