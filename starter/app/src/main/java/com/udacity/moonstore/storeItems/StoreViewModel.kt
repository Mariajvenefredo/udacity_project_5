package com.udacity.moonstore.storeItems

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.moonstore.api.models.Store
import com.udacity.moonstore.data.StockNotificationHelper
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.data.StoreDataSource
import kotlinx.coroutines.launch

class StoreViewModel(
    app: Application,
    private val dataSource: StoreDataSource
) : AndroidViewModel(app) {

    val stockNotificationStatus: MutableLiveData<StockNotificationStatus> = MutableLiveData()
    val availableStores: MutableLiveData<List<Store>> = MutableLiveData()

    val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    init {
        viewModelScope.launch {
            dataSource.addStoresToDatabase()
            getStores()
        }
    }

    private fun getStores() {
        val stores = mutableListOf<Store>()
        viewModelScope.launch {
            val storeList = dataSource.getStores()
            stores.addAll(storeList)
            availableStores.value = stores
        }
    }

    @TargetApi(29)
    val qPermissionList =
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

    val beforeQPermissionList =
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun updateStockNotificationStatus(
        activity: Activity,
        status: StockNotificationStatus
    ) {
        StockNotificationHelper.setStockNotificationPreference(
            activity,
            status
        )
        stockNotificationStatus.value = (status)
    }
}