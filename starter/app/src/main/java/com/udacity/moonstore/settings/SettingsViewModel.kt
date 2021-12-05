package com.udacity.moonstore.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.base.BaseViewModel
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.storeItems.StoreDataItem
import kotlinx.coroutines.launch

class SettingsViewModel(
    app: Application,
    private val dataSource: StoreDataSource
) : BaseViewModel(app) {

    fun getFavoriteItems(): List<StoreDataItem> {
        val items = mutableListOf<StoreDataItem>()
        viewModelScope.launch {
            items.addAll(dataSource.getStoreItems(StoreItemFilter.FAVORITES))
        }
        return items
    }
}