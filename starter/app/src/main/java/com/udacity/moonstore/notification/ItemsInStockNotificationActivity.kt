package com.udacity.moonstore.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem
import java.io.Serializable

class ItemsInStockNotificationActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_StoreItems = "EXTRA_StoreItems"
        private const val EXTRA_Store = "EXTRA_Store"

        fun newIntent(context: Context, store: Store, storeItems: List<StoreItem>): Intent {
            val intent = Intent(context, ItemsInStockNotificationActivity::class.java)
            intent.putExtra(EXTRA_StoreItems, storeItems.toTypedArray())
            intent.putExtra(EXTRA_Store, store as Serializable)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_in_stock_notification)

        if (intent.hasExtra(EXTRA_StoreItems)) {
            val items =
                (intent.getSerializableExtra(EXTRA_StoreItems) as Array<*>).map { value ->
                    value as StoreItem
                }
            val currentStore =
                intent.getSerializableExtra(EXTRA_Store) as Store

            setupNavigation(items, currentStore)
        }
    }

    private fun setupNavigation(items: List<StoreItem>, store: Store) {
        val bundle = Bundle()
        bundle.putParcelableArray("storeItems", items.toTypedArray())
        bundle.putParcelable("store", store)
        findNavController(R.id.nav_host_fragment_notification).setGraph(
            R.navigation.nav_graph_notification,
            bundle
        )
    }
}
