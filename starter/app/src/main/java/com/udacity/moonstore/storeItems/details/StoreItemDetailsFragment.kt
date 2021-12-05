package com.udacity.moonstore.storeItems.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.udacity.moonstore.R
import com.udacity.moonstore.base.BaseFragment
import com.udacity.moonstore.databinding.FragmentStoreItemDetailsBinding
import com.udacity.moonstore.storeItems.FavoriteAnimationHelper
import com.udacity.moonstore.storeItems.StoreDataItem
import com.udacity.moonstore.storeItems.StoreListViewModel
import com.udacity.moonstore.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreItemDetailsFragment : BaseFragment() {

    override val _viewModel: StoreListViewModel by viewModel()
    private val args: StoreItemDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentStoreItemDetailsBinding
    private lateinit var storeDataItem: StoreDataItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_store_item_details, container, false
            )

        setDisplayHomeAsUpEnabled(false)

        storeDataItem = args.storeDataItem
        binding.item = storeDataItem

        binding.favoriteIcon.setOnClickListener { view ->
            val animator = FavoriteAnimationHelper.createFavoriteAnimator(
                view as ImageView,
                storeDataItem.markedAsFavorite
            )

            animator.doOnEnd {
                _viewModel.changeFavoriteStatus(storeDataItem)
            }
            animator.start()
        }
        return binding.root
    }
}