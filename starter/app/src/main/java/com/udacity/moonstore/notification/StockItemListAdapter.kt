package com.udacity.moonstore.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.moonstore.databinding.StoreShortItemBinding
import com.udacity.moonstore.storeItems.models.StoreItem

class StockItemListAdapter(
    val navigateToDetails: (storeItem: StoreItem) -> Unit
) :
    ListAdapter<StoreItem, StockItemListAdapter.StoreDataItemGridViewHolder>(
        StoreDataItemDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreDataItemGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StoreDataItemGridViewHolder(
            StoreShortItemBinding.inflate(inflater)
        )
    }

    override fun onBindViewHolder(holder: StoreDataItemGridViewHolder, position: Int) {
        getItem(position).let { item ->
            holder.binding.layoutShortitem.setOnClickListener {
                navigateToDetails(item)
            }
            holder.bind(item)
        }
    }

    inner class StoreDataItemGridViewHolder(var binding: StoreShortItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoreItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    private class StoreDataItemDiffCallback : DiffUtil.ItemCallback<StoreItem>() {

        override fun areItemsTheSame(
            oldItem: StoreItem,
            newItem: StoreItem
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: StoreItem,
            newItem: StoreItem
        ): Boolean = oldItem.name == newItem.name

    }
}