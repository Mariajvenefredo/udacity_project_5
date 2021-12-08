package com.udacity.moonstore.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val myhelper: MyHelper by lazy { MyHelper() }

    override fun onReceive(context: Context, intent: Intent) {
       myhelper.onReceive(context, intent)
    }
}

class MyHelper : KoinComponent {

    fun onReceive(context: Context, intent: Intent) {
        GeofenceJobIntentService.enqueueWork(context, intent)
    }
}
