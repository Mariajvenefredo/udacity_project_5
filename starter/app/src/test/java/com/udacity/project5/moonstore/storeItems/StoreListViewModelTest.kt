package com.udacity.project5.moonstore.storeItems

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.moonstore.api.StoreItemFilter
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.storeItems.list.StoreListViewModel
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.project5.moonstore.data.FakeDataSource
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class StoreListViewModelTest {

    private lateinit var storeListViewModel: StoreListViewModel
    private lateinit var dataSource: FakeDataSource
    private lateinit var context: Application

    private val itemsList = mutableListOf(
        StoreItemDTO(1, "item1", "url1", 1.0, false),
        StoreItemDTO(2, "item2", "url2", 2.0, true),
        StoreItemDTO(3, "item3", "url3", 3.0, false),
    )
    private val storeItem = StoreItem(2, "item2", "url2", 2.0, true)
    private val storeList = mutableListOf(
        PhysicalStoreDTO(1, "store1", 0.1, 0.1),
        PhysicalStoreDTO(2, "store2", 0.2, 0.2)
    )
    private val storeWithStockList = mutableListOf(
        PhysicalStoreDTO(1, "store1", 0.1, 0.1),
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource(itemsList, storeList, storeWithStockList)
        context = ApplicationProvider.getApplicationContext()

        storeListViewModel = StoreListViewModel(context, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadStoreItems_listIsTheSame() {
        storeListViewModel.loadStoreItems()
        val viewModelList = storeListViewModel.storeItemList.value
        val dataList = itemsList.map { item -> item.toStoreDataItem() }
        assertEquals(viewModelList, dataList)
    }

    @Test
    fun loadStoreItems_itemsAreNotLoaded() {
        dataSource.shouldReturnError = true
        storeListViewModel = StoreListViewModel(context, dataSource)

        storeListViewModel.loadStoreItems()
        val viewModelList = storeListViewModel.storeItemList.value

        assertEquals(null, viewModelList)
    }

    @Test
    fun getStoresWithStock_storesAreRetrieved() {
        storeListViewModel.getStoresWithStock(storeItem)
        val stores = storeListViewModel.storesWithStock.value
        val dataList = storeWithStockList.map { item -> item.toStore() }
        assertEquals(stores, dataList)
    }

    @Test
    fun getStoresWithStock_storesAreNotRetrieved() {
        dataSource.shouldReturnError = true
        storeListViewModel.getStoresWithStock(storeItem)
        val stores = storeListViewModel.storesWithStock.value

        assertEquals(null, stores)
    }

    @Test
    fun updateFilter_storesAreFiltered() {
        storeListViewModel.updateFilter(StoreItemFilter.FAVORITES)
        val dataList = storeListViewModel.storeItemList.value
        val favoritesList = mutableListOf(storeItem)
        assertEquals(favoritesList, dataList)
    }

    @Test
    fun updateFavoriteStatus_zeroFavoritesExist() {
        storeListViewModel.changeFavoriteStatus(storeItem)
        storeListViewModel.updateFilter(StoreItemFilter.FAVORITES)
        val dataList = storeListViewModel.storeItemList.value
        val result = emptyList<StoreItem>()

        assertEquals(result, dataList)
    }
}