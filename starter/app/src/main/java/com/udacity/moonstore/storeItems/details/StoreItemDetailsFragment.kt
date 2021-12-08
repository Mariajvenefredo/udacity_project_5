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
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.moonstore.storeItems.list.StoreListViewModel
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreItemDetailsFragment : BaseFragment() {

    override val _viewModel: StoreListViewModel by viewModel()

    private lateinit var binding: FragmentStoreItemDetailsBinding
    private lateinit var storeItem: StoreItem

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
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeItem = arguments?.get("storeItem") as StoreItem

        binding.item = storeItem
        _viewModel.getStoresWithStock(storeItem)

        _viewModel.storesWithStock.observe(viewLifecycleOwner, { stores ->
            binding.storesWithStock.text = stores
        })

        binding.favoriteIcon.setOnClickListener { view ->
            val animator = FavoriteAnimationHelper.createFavoriteAnimator(
                view as ImageView,
                storeItem.markedAsFavorite
            )

            animator.doOnEnd {
                _viewModel.changeFavoriteStatus(storeItem)
            }
            animator.start()
        }
    }
}