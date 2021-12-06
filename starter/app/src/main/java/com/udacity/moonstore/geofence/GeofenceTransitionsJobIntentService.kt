package com.udacity.moonstore.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.storeItems.StoreItem
import com.udacity.moonstore.utils.sendNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
    private val storeRepository: StoreDataSource by inject()

    companion object {
        private const val JOB_ID = 573

        private const val ACTION_GEOFENCE_EVENT =
            "moonstore.ACTION_GEOFENCE_EVENT"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofenceEvent = GeofencingEvent.fromIntent(intent)

            if (geofenceEvent.hasError())
                return

            if (geofenceEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                val geofences = geofenceEvent.triggeringGeofences
                val storeId = geofences.firstOrNull()?.requestId
                if (storeId != null) {
                    CoroutineScope(coroutineContext).launch {
                        val itemsInStock = requestItemsInStock(storeId)
                        validateAndSendNotification(storeId, itemsInStock)
                    }
                }
            }
        }
    }

    private suspend fun requestItemsInStock(storeId: String): List<StoreItem> {
        val itemsInStock = mutableListOf<StoreItem>()

        val favorites = storeRepository.getStoreItems(StoreItemFilter.FAVORITES)
        favorites.forEach { storeDataItem ->
            val hasStock =
                storeRepository.getStoreStockForItem(storeId, storeDataItem.id.toString())
            if (hasStock) {
                itemsInStock.add(storeDataItem)
            }
        }
        return itemsInStock
    }

    //TODO: get the request id of the current geofence
    private fun validateAndSendNotification(storeId: String, itemsInStock: List<StoreItem>) {
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            val result = storeRepository.getStore(storeId)

            if (result != null) {//is Result.Success<Store>) {
                sendNotification(
                    this@GeofenceTransitionsJobIntentService,
                    result,
                    itemsInStock
                )
            }
        }
    }
}
