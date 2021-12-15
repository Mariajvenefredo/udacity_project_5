package com.udacity.moonstore.storeItems

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.opengl.Visibility
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.moonstore.data.StockNotificationHelper
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.local.Result
import com.udacity.moonstore.storeItems.models.Store
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.launch

class StoreViewModel(
    app: Application,
    private val dataSource: StoreDataSource
) : AndroidViewModel(app) {

    val availableStores: MutableLiveData<List<Store>> = MutableLiveData()
    val stockNotificationStatus: Subject<StockNotificationStatus> =
        BehaviorSubject.create()

    val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    @VisibleForTesting
    fun getStores() {
        val stores = mutableListOf<Store>()
        viewModelScope.launch {
            val storeList = dataSource.getStores()
            if (storeList is Result.Success<List<Store>>) {
                stores.addAll(storeList.data)
                availableStores.value = stores
            }
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
        changed: Boolean,
        status: StockNotificationStatus
    ) {
        if (changed) {
            stockNotificationStatus.onNext(status)
        }
    }

    fun initializeComponents() {
        viewModelScope.launch {
            dataSource.addStoresToDatabase()
            getStores()
        }
    }
}