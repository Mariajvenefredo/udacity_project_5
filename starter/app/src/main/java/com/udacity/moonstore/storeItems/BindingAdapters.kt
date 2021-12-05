package com.udacity.moonstore.storeItems

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.list.StoreListAdapter

@BindingAdapter("favoriteStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, markedAsFavorite: Boolean) {
    if (markedAsFavorite) {
        imageView.setImageResource(R.drawable.heart_filled)
    } else {
        imageView.setImageResource(R.drawable.empty_heart)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_picture)
            )
            .into(imgView)
    }
}

@BindingAdapter("dataList")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<StoreDataItem>?) {
    val adapter = recyclerView.adapter as StoreListAdapter
    adapter.submitList(data)
}

@BindingAdapter("priceText")
fun bindPrice(textView: TextView, price: Double?) {
    val priceString = price.toString()
    textView.text = priceString + "€"
}
