package com.udacity.moonstore.storeItems.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.moonstore.databinding.StoreItemBinding
import com.udacity.moonstore.storeItems.FavoriteAnimationHelper
import com.udacity.moonstore.storeItems.StoreItem

class StoreListAdapter(
    val changeFavoriteStatus: (storeItem: StoreItem) -> Unit,
    val navigateToDetails: (storeItem: StoreItem) -> Unit
) :
    ListAdapter<StoreItem, StoreListAdapter.StoreDataItemGridViewHolder>(
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
        item: StoreItem
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

        fun bind(item: StoreItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    private class StoreDataItemGridDiffCallback : DiffUtil.ItemCallback<StoreItem>() {

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