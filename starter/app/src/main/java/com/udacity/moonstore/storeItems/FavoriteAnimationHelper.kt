package com.udacity.moonstore.storeItems

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.widget.ImageView
import com.udacity.moonstore.R

object FavoriteAnimationHelper {
    fun createFavoriteAnimator(image: ImageView, markedAsFavorite: Boolean): Animator {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(image, scaleX, scaleY)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.duration = 500
        val resource =
            if (markedAsFavorite) R.drawable.empty_heart else R.drawable.heart_filled

        image.setImageResource(resource)
        return animator
    }
}