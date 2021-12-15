package com.udacity.moonstore.storeItems.list

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.internal.matcher.RecyclerViewItemCountAssertion
import com.udacity.moonstore.FakeDataSource
import com.udacity.moonstore.R
import com.udacity.moonstore.data.StockNotificationHelper
import com.udacity.moonstore.data.StockNotificationHelper.getStockNotificationPreference
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.moonstore.util.withImageDrawable
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
@MediumTest
class StoreListFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var appContext: Application

    private val storeItems = mutableListOf(
        StoreItem(1, "Item1", "Url1", 10.0, false),
        StoreItem(2, "Item2", "Url2", 15.0, true),
        StoreItem(3, "Item3", "Url3", 20.0, false)
    )

    @Before
    fun startKoin() {
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        val list = storeItems.map { i -> i.toStoreItemDTO() }.toMutableList()
        val dataSource = FakeDataSource(list)

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
    fun fragmentIsShowing_itemListAppears() {
        mockkObject(StockNotificationHelper)
        every { getStockNotificationPreference(any()) } returns StockNotificationStatus.NOTIF_OFF

        val scenario = launchFragmentInContainer<StoreListFragment>(
            Bundle(), R.style.AppTheme
        )
        val mockNavController = mock(NavController::class.java)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(
            allOf(
                withId(R.id.refreshLayout),
                hasDescendant(withImageDrawable(R.drawable.store_logo)),
                isDisplayed()
            )
        )

        assertDisplayedAtPosition(
            R.id.storeRecyclerView,
            0,
            R.id.storeItemTitle,
            "Item1"
        )

        assertDisplayedAtPosition(
            R.id.storeRecyclerView,
            1,
            R.id.storeItemTitle,
            "Item2"
        )

        assertDisplayedAtPosition(
            R.id.storeRecyclerView,
            2,
            R.id.storeItemTitle,
            "Item3"
        )

        assertCustomAssertionAtPosition(
            R.id.storeRecyclerView,
            0,
            R.id.storeItemPrice,
            matches(withText(containsString("10.0")))
        )

        assertCustomAssertionAtPosition(
            R.id.storeRecyclerView,
            1,
            R.id.storeItemPrice,
            matches(withText(containsString("15.0")))
        )

        assertCustomAssertionAtPosition(
            R.id.storeRecyclerView,
            2,
            R.id.storeItemPrice,
            matches(withText(containsString("20.0")))
        )
    }
}