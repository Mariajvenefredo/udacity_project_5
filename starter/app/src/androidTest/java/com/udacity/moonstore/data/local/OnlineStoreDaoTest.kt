package com.udacity.moonstore.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.data.local.StoreDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class OnlineStoreDaoTest {

    private lateinit var database: StoreDatabase

    private val storeItems = mutableListOf(
        StoreItemDTO(1, "Item1", "Url1", 10.0, false),
        StoreItemDTO(2, "Item2", "Url2", 15.0, true),
        StoreItemDTO(3, "Item3", "Url3", 20.0, false)
    )
    private val newItem = StoreItemDTO(4, "Item4", "Url4", 10.0, false)

    private val stores = mutableListOf(
        PhysicalStoreDTO(1, "store1", 0.1, 0.1),
        PhysicalStoreDTO(2, "store2", 0.2, 0.2),
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertStoreItemsAndGetAll() = runBlockingTest {
        database.onlineStoreDao().insertStoreItems(*storeItems.toTypedArray())

        val loaded = database.onlineStoreDao().getStoreItems()

        val firstElement = loaded?.get(0)
        val secondElement = loaded?.get(1)
        val thirdElement = loaded?.get(2)

        assertThat(loaded).isNotNull()
        assertThat(loaded?.count() == 3)
        assertThat(firstElement?.storeItemEquals(storeItems[0]))
        assertThat(secondElement?.storeItemEquals(storeItems[1]))
        assertThat(thirdElement?.storeItemEquals(storeItems[2]))
    }

    @Test
    fun insertStoresAndGetAll() = runBlockingTest {
        database.onlineStoreDao().insertStores(*stores.toTypedArray())

        val loaded = database.onlineStoreDao().getPhysicalStores()

        assertThat(loaded).isNotNull()
        val firstElement = loaded[0]
        val secondElement = loaded[1]

        assertThat(firstElement.storeEquals(stores[0]))
        assertThat(secondElement.storeEquals(stores[1]))
    }

    @Test
    fun insertStoreItemAndGetItem() = runBlockingTest {
        database.onlineStoreDao().insertStoreItems(*storeItems.toTypedArray())
        database.onlineStoreDao().insertStoreItem(newItem)
        val loaded = database.onlineStoreDao().getStoreItems()

        val newElement = loaded?.get(3)
        assertThat(loaded).isNotNull()
        assertThat(loaded?.count() == 4)
        assertThat(newElement?.let { newItem.storeItemEquals(it) })

    }

    @Test
    fun getStoreById() = runBlockingTest {
        database.onlineStoreDao().insertStores(*stores.toTypedArray())
        val store = database.onlineStoreDao().getStore(1)
        assertThat(store?.storeEquals(stores[0]))
    }

    @Test
    fun getFavoriteItems() = runBlockingTest {
        database.onlineStoreDao().insertStoreItems(*storeItems.toTypedArray())
        val favorites = database.onlineStoreDao().getFavoriteItems()
        assertThat(favorites?.get(0)?.storeItemEquals(storeItems[1]))
    }

    @Test
    fun deleteStoreItemAndTryGetShouldBeEmpty() = runBlockingTest {
        database.onlineStoreDao().insertStoreItems(*storeItems.toTypedArray())
        database.onlineStoreDao().deleteStoreItemFromLocal(1)

        val items = database.onlineStoreDao().getStoreItems()
        val exists = items?.firstOrNull { item -> item.id == 1L }

        assertNull(exists)
    }

    private fun StoreItemDTO.storeItemEquals(storeItemDTO: StoreItemDTO): Boolean {
        if ((id != storeItemDTO.id) or
            (name != storeItemDTO.name) or
            (markedAsFavorite != storeItemDTO.markedAsFavorite) or
            (price != storeItemDTO.price) or
            (url != storeItemDTO.url)
        ) {
            return false
        }
        return true
    }

    private fun PhysicalStoreDTO.storeEquals(physicalStoreDTO: PhysicalStoreDTO): Boolean {
        if ((id != physicalStoreDTO.id) or
            (name != physicalStoreDTO.name) or
            (latitude != physicalStoreDTO.latitude) or
            (longitude != physicalStoreDTO.longitude)
        ) {
            return false
        }
        return true
    }
}