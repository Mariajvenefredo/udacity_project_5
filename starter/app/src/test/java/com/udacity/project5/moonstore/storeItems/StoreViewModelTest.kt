package com.udacity.project5.moonstore.storeItems

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.models.StoreItem
import com.udacity.project5.moonstore.data.FakeDataSource
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.subjects.BehaviorSubject
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
class StoreViewModelTest {

    private lateinit var storeViewModel: StoreViewModel
    private lateinit var dataSource: FakeDataSource
    private lateinit var context: Application

    private val itemsList = mutableListOf(
        StoreItemDTO(1, "item1", "url1", 1.0, false),
        StoreItemDTO(2, "item2", "url2", 2.0, true),
        StoreItemDTO(3, "item3", "url3", 3.0, false),
    )
    private val storeList = mutableListOf(
        PhysicalStoreDTO(1, "store1", 0.1, 0.1),
        PhysicalStoreDTO(2, "store2", 0.2, 0.2)
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource(itemsList, storeList)
        context = ApplicationProvider.getApplicationContext()
        storeViewModel = StoreViewModel(context, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getStores_storesAreRetrieved() {
        storeViewModel.getStores()
        val vmStores = storeViewModel.availableStores.value
        val stores = storeList.map { store ->
            store.toStore()
        }
        assertEquals(stores, vmStores)
    }

    @Test
    fun updateStockNotificationStatus_publishedEvent() {
        val testObserver = TestObserver<StockNotificationStatus>()
        storeViewModel.stockNotificationStatus.subscribe(testObserver)
        storeViewModel.updateStockNotificationStatus(true, StockNotificationStatus.NOTIF_ON)

        testObserver.assertValue(StockNotificationStatus.NOTIF_ON)
    }
}