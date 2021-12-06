package com.udacity.moonstore.data

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity

object StockNotificationHelper {

    fun setStockNotificationPreference(
        activity: Activity,
        answer: StockNotificationStatus
    ) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(STOCK_NOFIF_KEY, answer.name)
            apply()
        }
    }

    fun getStockNotificationPreference(activity: FragmentActivity): String? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            return sharedPref.getString(STOCK_NOFIF_KEY, StockNotificationStatus.NEVER_DEFINED.name)
        }
        return StockNotificationStatus.NEVER_DEFINED.name
    }

    private const val STOCK_NOFIF_KEY = "STOCK_NOTFIF"
}

enum class StockNotificationStatus {
    NEVER_DEFINED,
    NOTIF_ON,
    NOTIF_OFF
}