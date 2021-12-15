package com.udacity.moonstore.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.udacity.moonstore.BuildConfig
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, store: Store, storeItems: List<StoreItem>) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent =
        ItemsInStockNotificationActivity.newIntent(context.applicationContext, store, storeItems)

    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(ItemsInStockNotificationActivity::class.java)
        .addNextIntent(intent)
    val notificationPendingIntent = stackBuilder
        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.moonstore_launcher_round)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentIntent(notificationPendingIntent)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(String.format(context.getString(R.string.notification_text), store.name))
        )
        .setAutoCancel(true)
        .build()

    notificationManager.notify(getUniqueId(), notification)
}

private fun getUniqueId() = 16