package com.udacity.moonstore.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.moonstore.R
import com.udacity.moonstore.base.BaseFragment
import com.udacity.moonstore.data.StockNotificationHelper.getStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationHelper.setStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment() {

    override val _viewModel: SettingsViewModel by viewModel()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.settingsItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setStockNotificationPreference(requireActivity(), StockNotificationStatus.NOTIF_ON)
            } else {
                setStockNotificationPreference(requireActivity(), StockNotificationStatus.NOTIF_OFF)
            }
        }

        val notifStatus = getStockNotificationPreference(requireActivity())
        if (notifStatus == StockNotificationStatus.NOTIF_ON.name) {
            binding.notificationStatus = true
        }
    }
}