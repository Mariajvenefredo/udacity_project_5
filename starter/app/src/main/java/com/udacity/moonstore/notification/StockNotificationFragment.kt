package com.udacity.moonstore.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.udacity.moonstore.R
import com.udacity.moonstore.databinding.FragmentNotificationStockBinding
import com.udacity.moonstore.utils.setDisplayHomeAsUpEnabled

class StockNotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationStockBinding
    private val args: StockNotificationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_notification_stock, container, false
            )

        setDisplayHomeAsUpEnabled(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = args.storeItems
        val currentStore = args.store

        binding.lifecycleOwner = this
        binding.apply {
            val adapter = StockItemListAdapter { item ->
                val bundle = bundleOf("storeItem" to item)
                findNavController().navigate(R.id.toDetails, bundle)
            }
            storeNotificationRecyclerView.adapter = adapter
            store = currentStore
            adapter.submitList(items.toList())

            executePendingBindings()
        }
    }
}