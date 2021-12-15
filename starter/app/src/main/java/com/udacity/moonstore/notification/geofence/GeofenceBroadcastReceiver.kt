package com.udacity.moonstore.notification.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val helper: ReceiverHelper by lazy { ReceiverHelper() }

    override fun onReceive(context: Context, intent: Intent) {
        helper.onReceive(context, intent)
    }
}

class ReceiverHelper : KoinComponent {

    fun onReceive(context: Context, intent: Intent) {
        GeofenceJobIntentService.enqueueWork(context, intent)
    }
}
