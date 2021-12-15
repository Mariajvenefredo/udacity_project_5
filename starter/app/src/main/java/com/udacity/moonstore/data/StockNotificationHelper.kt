package com.udacity.moonstore.data

import android.app.Activity
import android.content.Context

object StockNotificationHelper {

    fun setStockNotificationPreference(
        activity: Activity,
        answer: StockNotificationStatus
    ): Boolean {
        if (answer == getStockNotificationPreference(activity)) return false

        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return false
        with(sharedPref.edit()) {
            putString(STOCK_NOFIF_KEY, answer.name)
            apply()
        }
        return true
    }

    fun getStockNotificationPreference(activity: Activity): StockNotificationStatus {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            val preferenceString =
                sharedPref.getString(STOCK_NOFIF_KEY, StockNotificationStatus.NEVER_DEFINED.name)
            return StockNotificationStatus.fromString(preferenceString!!)
        }
        return StockNotificationStatus.NEVER_DEFINED
    }

    private const val STOCK_NOFIF_KEY = "STOCK_NOTFIF"
}

enum class StockNotificationStatus {
    NEVER_DEFINED,
    NOTIF_ON,
    NOTIF_OFF;

    companion object {

        private val ALL = listOf(NEVER_DEFINED, NOTIF_OFF, NOTIF_ON)

        fun fromString(string: String): StockNotificationStatus =
            ALL.firstOrNull { preference -> preference.name == string } ?: NEVER_DEFINED

    }
}