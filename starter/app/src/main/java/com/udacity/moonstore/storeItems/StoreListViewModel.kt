package com.udacity.moonstore.storeItems

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.base.BaseViewModel
import com.udacity.moonstore.data.StoreDataSource
import kotlinx.coroutines.launch

class StoreListViewModel(
    app: Application,
    private val dataSource: StoreDataSource
) : BaseViewModel(app) {

    val storeItemList = MutableLiveData<List<StoreDataItem>>()
    private val filter = MutableLiveData(StoreItemFilter.ALL)

    init {
        viewModelScope.launch {
            dataSource.addStoreInformationToDatabase()
        }
    }

    fun loadStoreItems() {
        showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = filter.value?.let { dataSource.getStoreItems(it) }
            storeItemList.value = result!!
            showLoading.value = false
/*            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<StoreDataItem>()
                    dataList.addAll((result.data as List<FavoriteItemDTO>).map { reminder ->
                        //map the reminder data from the DB to the be ready to be displayed on the UI
                        StoreDataItem(
                            reminder.title,
                            reminder.description,
                            reminder.location,
                            reminder.latitude,
                            reminder.longitude,
                            reminder.id
                        )
                    })
                    storeItemList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }*/

            //check if no data has to be shown
            //invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = storeItemList.value == null || storeItemList.value!!.isEmpty()
    }

    fun updateFilter(filtered: StoreItemFilter) =
        viewModelScope.launch {
            filter.value = filtered
            loadStoreItems()
        }

    fun changeFavoriteStatus(storeItem: StoreDataItem) {
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