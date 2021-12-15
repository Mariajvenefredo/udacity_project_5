package com.udacity.moonstore.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

private fun sameBitmap(context: Context, drawable: Drawable, resourceId: Int): Boolean {
    var drawable: Drawable? = drawable
    var otherDrawable: Drawable = context.resources.getDrawable(resourceId)
    if (drawable == null || otherDrawable == null) {
        return false
    }
    if (drawable is StateListDrawable && otherDrawable is StateListDrawable) {
        drawable = drawable.getCurrent()
        otherDrawable = otherDrawable.getCurrent()
    }
    if (drawable is BitmapDrawable) {
        val bitmap = drawable.bitmap
        val otherBitmap = (otherDrawable as BitmapDrawable).bitmap
        return bitmap.sameAs(otherBitmap)
    }
    return false
}

fun withImageDrawable(resourceId: Int): Matcher<View?>? {
    return object : BoundedMatcher<View?, ImageView>(ImageView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has image drawable resource $resourceId")
        }

        override fun matchesSafely(imageView: ImageView): Boolean {
            return sameBitmap(imageView.context, imageView.drawable, resourceId)
        }
    }
}