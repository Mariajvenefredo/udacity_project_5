package com.udacity.moonstore.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.moonstore.R
import com.udacity.moonstore.api.models.Store
import com.udacity.moonstore.databinding.ActivityReminderDescriptionBinding
import com.udacity.moonstore.storeItems.StoreItem
import java.io.Serializable

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ItemsInStockActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_StoreItems = "EXTRA_StoreItems"
        private const val EXTRA_Store = "EXTRA_Store"

        fun newIntent(context: Context, store: Store, storeItems: List<StoreItem>): Intent {
            val intent = Intent(context, ItemsInStockActivity::class.java)
            intent.putExtra(EXTRA_StoreItems, storeItems.toTypedArray())
            intent.putExtra(EXTRA_Store, store as Serializable)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        if (intent.hasExtra(EXTRA_StoreItems)) {
            val items =
                (intent.getSerializableExtra(EXTRA_StoreItems) as Array<*>).map { value ->
                    value as StoreItem
                }

            val store =
                intent.getSerializableExtra(EXTRA_Store) as Store

            binding.apply {
                //reminderDataItem = notifReminderDataItem
                //executePendingBindings()
            }
        }
    }
}
