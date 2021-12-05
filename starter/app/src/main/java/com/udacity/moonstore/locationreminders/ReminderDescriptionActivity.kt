package com.udacity.moonstore.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.moonstore.R
import com.udacity.moonstore.databinding.ActivityReminderDescriptionBinding
import com.udacity.moonstore.storeItems.StoreDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, storeDataItem: StoreDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, storeDataItem)
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
        if (intent.hasExtra(EXTRA_ReminderDataItem)) {
            val notifReminderDataItem =
                intent.getSerializableExtra(EXTRA_ReminderDataItem) as StoreDataItem

            binding.apply {
                reminderDataItem = notifReminderDataItem
                executePendingBindings()
            }
        }
    }
}
