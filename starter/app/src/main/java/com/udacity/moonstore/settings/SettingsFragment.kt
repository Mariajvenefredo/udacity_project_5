package com.udacity.moonstore.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.moonstore.R
import com.udacity.moonstore.data.StockNotificationHelper.getStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.databinding.FragmentSettingsBinding
import com.udacity.moonstore.storeItems.StoreActivity
import com.udacity.moonstore.storeItems.StoreViewModel

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
        viewModel =
            ViewModelProvider(requireActivity() as StoreActivity)[StoreViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.settingsItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.updateStockNotificationStatus(
                    requireActivity(),
                    StockNotificationStatus.NOTIF_ON
                )
            } else {
                viewModel.updateStockNotificationStatus(
                    requireActivity(),
                    StockNotificationStatus.NOTIF_OFF
                )
            }
        }
        viewModel.stockNotificationStatus.observe(viewLifecycleOwner, { status ->
            binding.settingsItem.isChecked = status == StockNotificationStatus.NOTIF_ON
        })

        val notifStatus = getStockNotificationPreference(requireActivity())
        if (notifStatus == StockNotificationStatus.NOTIF_ON.name) {
            binding.notificationStatus = true
        }
    }
}