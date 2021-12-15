package com.udacity.moonstore.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.moonstore.R
import com.udacity.moonstore.data.StockNotificationHelper
import com.udacity.moonstore.data.StockNotificationHelper.getStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.databinding.FragmentSettingsBinding
import com.udacity.moonstore.storeItems.StoreActivity
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.utils.setDisplayHomeAsUpEnabled

class SettingsFragment : Fragment() {

    private lateinit var viewModel: StoreViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_settings, container, false
            )

        if (requireActivity() is StoreActivity) {
            viewModel =
                ViewModelProvider(requireActivity() as StoreActivity)[StoreViewModel::class.java]

            viewModel.stockNotificationStatus.subscribe { status ->
                binding.settingsItem.isChecked = status == StockNotificationStatus.NOTIF_ON
            }
        }

        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.settingsItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val changed = StockNotificationHelper.setStockNotificationPreference(
                    requireActivity(),
                    StockNotificationStatus.NOTIF_ON
                )
                viewModel.updateStockNotificationStatus(
                    changed,
                    StockNotificationStatus.NOTIF_ON
                )
            } else {
                val changed = StockNotificationHelper.setStockNotificationPreference(
                    requireActivity(),
                    StockNotificationStatus.NOTIF_OFF
                )
                viewModel.updateStockNotificationStatus(
                    changed,
                    StockNotificationStatus.NOTIF_OFF
                )
            }
        }

        val notifStatus = getStockNotificationPreference(requireActivity())
        if (notifStatus == StockNotificationStatus.NOTIF_ON) {
            binding.notificationStatus = true
        }
    }
}