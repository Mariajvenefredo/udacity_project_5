package com.udacity.moonstore.locationreminders.savereminder

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.moonstore.R
import com.udacity.moonstore.base.BaseViewModel
import com.udacity.moonstore.storeItems.models.StoreItem

class SaveReminderViewModel(
    val app: Application,

    // val dataSource: ReminderDataSource
) :
    BaseViewModel(app) {
    val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

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

    val reminderTitle = MutableLiveData<String?>()
    val reminderDescription = MutableLiveData<String?>()
    val reminderSelectedLocationStr = MutableLiveData<String?>()
    val selectedPOI = MutableLiveData<PointOfInterest?>()
    val latitude = MutableLiveData<Double?>()
    val longitude = MutableLiveData<Double?>()

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(store: StoreItem): Boolean {
        if (validateEnteredData(store)) {
            saveReminder(store)
            return true
        }
        return false
    }

    /**
     * Save the reminder to the data source
     */
    fun saveReminder(store: StoreItem) {
        showLoading.value = true
/*        viewModelScope.launch {
            dataSource.saveReminder(
                FavoriteItemDTO(
                    storeData.title,
                    storeData.description,
                    storeData.location,
                    storeData.latitude,
                    storeData.longitude,
                    storeData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }*/
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(store: StoreItem): Boolean {
        if (store.name.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

/*        if (storeData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }*/
        return true
    }
}