package com.udacity.moonstore.storeItems.details

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.udacity.moonstore.FakeDataSource
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.list.StoreListViewModel
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.moonstore.util.withImageDrawable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@MediumTest
class StoreItemDetailsFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var appContext: Application

    private val storeItem =
        StoreItem(1, "Item1", "Url1", 10.0, false)

    @Before
    fun startKoin() {
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        val dataSource = FakeDataSource()

        val myModule = module {

            viewModel {
                StoreListViewModel(
                    appContext,
                    dataSource
                )
            }
            viewModel {
                StoreViewModel(
                    appContext,
                    dataSource
                )
            }
        }

        GlobalContext.startKoin {
            modules(listOf(myModule))
        }
    }

    @After
    fun closeKoin() {
        stopKoin()
    }

    @Test
    fun fragmentIsShowing_storeItemDetailsAppears() {
        launchFragmentInContainer<StoreItemDetailsFragment>(
            bundleOf(
                "storeItem" to storeItem,
            ), R.style.AppTheme
        )

        Espresso.onView(withId(R.id.itemDetailsName))
            .check(matches(isDisplayed()))
            .check(matches(withText(storeItem.name)))

        Espresso.onView(withId(R.id.itemDetailsPrice))
            .check(matches(isDisplayed()))
            .check(matches(withText(containsString(storeItem.price.toString()))))

        Espresso.onView(withId(R.id.storeItemDetailsFavoriteIcon))
            .check(matches(isDisplayed()))
            .check(matches(withImageDrawable(R.drawable.empty_heart)))
    }
}