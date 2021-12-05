package com.udacity.moonstore.storeItems.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.moonstore.databinding.StoreItemBinding
import com.udacity.moonstore.storeItems.FavoriteAnimationHelper
import com.udacity.moonstore.storeItems.StoreDataItem

class StoreListAdapter(
    val changeFavoriteStatus: (storeItem: StoreDataItem) -> Unit,
    val navigateToDetails: (storeItem: StoreDataItem) -> Unit
) :
    ListAdapter<StoreDataItem, StoreListAdapter.StoreDataItemGridViewHolder>(
        StoreDataItemGridDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreDataItemGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StoreDataItemGridViewHolder(
            StoreItemBinding.inflate(inflater)
        )
    }

    override fun onBindViewHolder(holder: StoreDataItemGridViewHolder, position: Int) {
        getItem(position).let { item ->
            holder.binding.favoriteIcon.setOnClickListener {
                startFavoriteButtonAnimation(holder, item)
            }
            holder.binding.storeitemPicture.setOnClickListener {
                navigateToDetails(item)
            }
            holder.bind(item)
        }
    }

    private fun startFavoriteButtonAnimation(
        holder: StoreDataItemGridViewHolder,
        item: StoreDataItem
    ) {
        val image = holder.binding.favoriteIcon
        val animator = FavoriteAnimationHelper.createFavoriteAnimator(image, item.markedAsFavorite)

        animator.doOnEnd {
            changeFavoriteStatus(item)
        }
        animator.start()
    }

    inner class StoreDataItemGridViewHolder(var binding: StoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoreDataItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    private class StoreDataItemGridDiffCallback : DiffUtil.ItemCallback<StoreDataItem>() {

        override fun areItemsTheSame(
            oldItem: StoreDataItem,
            newItem: StoreDataItem
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: StoreDataItem,
            newItem: StoreDataItem
        ): Boolean = oldItem.name == newItem.name

    }
}