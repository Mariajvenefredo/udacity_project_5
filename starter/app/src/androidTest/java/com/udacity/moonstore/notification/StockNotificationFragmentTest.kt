package com.udacity.moonstore.notification

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.storeItems.models.StoreItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@MediumTest
class StockNotificationFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val storeItems = mutableListOf(
        StoreItem(1, "Item1", "Url1", 10.0, false),
        StoreItem(2, "Item2", "Url2", 15.0, true),
        StoreItem(3, "Item3", "Url3", 20.0, false)
    )

    private val store = Store(2, "store2", 0.2, 0.2)

    @After
    fun closeKoin() {
        stopKoin()
    }

    @Test
    fun fragmentIsShowing_notificationAppears() {
        launchFragmentInContainer<StockNotificationFragment>(
            bundleOf(
                "storeItems" to storeItems.toTypedArray(),
                "store" to store
            ), R.style.AppTheme
        )

        onView(withId(R.id.notification_long_description))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.notification_screen_description)))

        onView(withId(R.id.store_textView))
            .check(matches(isDisplayed()))
            .check(matches(withText(store.name)))

        onView(
            allOf(
                withId(R.id.storeNotificationRecyclerView),
                hasDescendant(withText("Item1")),
                hasDescendant(withText("Item2")),
                hasDescendant(withText("Item3")),
                isDisplayed()
            )
        )
    }
}