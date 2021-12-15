package com.udacity.moonstore.storeItems.list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.base.BaseViewModel
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.local.Result
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem
import kotlinx.coroutines.launch

class StoreListViewModel(
    app: Application,
    private val dataSource: StoreDataSource
) : BaseViewModel(app) {

    val storeItemList = MutableLiveData<List<StoreItem>>()
    val storesWithStock = MutableLiveData<List<Store>>()

    private val filter = MutableLiveData(StoreItemFilter.ALL)

    init {
        viewModelScope.launch {
            dataSource.addStoreItemsToDatabase()
            loadStoreItems()
        }
    }

    fun loadStoreItems() {
        showLoading.value = true
        viewModelScope.launch {

            val result = filter.value?.let { dataSource.getStoreItems(it) }

            if (result is Result.Success<List<StoreItem>>) {
                storeItemList.value = result.data
                showLoading.value = false
            } else if (result is Result.Error) {
                showSnackBar.value = result.message
            }
            invalidateShowNoData()
        }
    }

    private fun invalidateShowNoData() {
        showNoData.value = storeItemList.value == null || storeItemList.value!!.isEmpty()
    }

    fun getStoresWithStock(storeItem: StoreItem) {
        viewModelScope.launch {
            val stores = dataSource.getStoresWithStockForItem(storeItem)

            if (stores is Result.Success<List<Store>>) {
                storesWithStock.value = stores.data
            }
        }
    }

    fun updateFilter(filtered: StoreItemFilter) =
        viewModelScope.launch {
            filter.value = filtered
            loadStoreItems()
        }

    fun changeFavoriteStatus(storeItem: StoreItem) {
        viewModelScope.launch {

            storeItem.markedAsFavorite = !storeItem.markedAsFavorite
            viewModelScope.launch {
                dataSource.updateFavoriteStatus(storeItem)

                //only updating the live data list so that it does not render again
                storeItemList.value?.firstOrNull { item ->
                    item == storeItem
                }?.markedAsFavorite = storeItem.markedAsFavorite
            }
        }
    }
}