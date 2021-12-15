package com.udacity.moonstore.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class StoreRepositoryTest {

    private lateinit var localRepository: StoreRepository
    private lateinit var database: StoreDatabase

    private val storeItems = mutableListOf(
        StoreItemDTO(1, "Item1", "Url1", 10.0, false),
        StoreItemDTO(2, "Item2", "Url2", 15.0, true),
        StoreItemDTO(3, "Item3", "Url3", 20.0, false)
    )

    private val item3asFavorite = StoreItemDTO(3, "Item3", "Url3", 20.0, true)

    private val stores = mutableListOf(
        PhysicalStoreDTO(1, "store1", 0.1, 0.1),
        PhysicalStoreDTO(2, "store2", 0.2, 0.2),
    )
    private val storesWithStock = mutableListOf(
        PhysicalStoreDTO(2, "store2", 0.2, 0.2),
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()

        localRepository = spyk(StoreRepository(database.onlineStoreDao()))
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun addStoreItemsToDatabase_getsItems() = runBlocking {
        coEvery { localRepository.getItemsAPI() } returns storeItems
        localRepository.addStoreItemsToDatabase()

        val result = localRepository.getStoreItems()
        assertThat(result is Result.Success)
        val items = (result as Result.Success).data
        items.forEachIndexed { index, item ->
            assertThat(item.toStoreItemDTO().storeItemEquals(storeItems[index])).isTrue()
        }
    }

    @Test
    fun addStoresToDatabase_getsStores() = runBlocking {
        coEvery { localRepository.getStoresAPI() } returns stores

        localRepository.addStoresToDatabase()

        val result = localRepository.getStores()
        assertThat(result is Result.Success)
        val items = (result as Result.Success).data
        items.forEachIndexed { index, store ->
            assertThat(store.toPhysicalStoreDTO().storeEquals(stores[index])).isTrue()
        }
    }

    @Test
    fun updateFavoriteStatus_updatesStatus() = runBlocking {
        coEvery { localRepository.getStoresAPI() } returns stores

        localRepository.updateFavoriteStatus(item3asFavorite.toStoreDataItem())

        val result = localRepository.getStoreItems()
        assertThat(result is Result.Success)
        val items = (result as Result.Success).data
        val resultItem = items.firstOrNull { item ->
            item.id == item3asFavorite.id
        }
        assertThat(resultItem?.markedAsFavorite).isTrue()
    }

    @Test
    fun getStoresWithStockForItem_getsStores() = runBlocking {
        coEvery { localRepository.getStoresWithStockAPI(any()) } returns storesWithStock.map { s -> s.toStore() }

        val result =
            localRepository.getStoresWithStockForItem(storeItems.first().toStoreDataItem())

        assertThat(result is Result.Success)
        val items = (result as Result.Success).data
        items.forEachIndexed { index, store ->
            assertThat(store.toPhysicalStoreDTO().storeEquals(storesWithStock[index])).isTrue()
        }
    }

    @Test
    fun getStoreStockForItem_getsStock() = runBlocking {
        coEvery { localRepository.getStockForPieceAPI(any(), any()) } returns "true"

        val result =
            localRepository.getStoreStockForItem("1", "1")

        assertThat(result is Result.Success)
        val success = (result as Result.Success).data
        assertThat(success).isTrue()
    }

    @Test
    fun getStore_getsStore() = runBlocking {
        coEvery { localRepository.getStoresAPI() } returns stores

        localRepository.addStoresToDatabase()

        val result =
            localRepository.getStore("1")
        val expected = stores.first { store -> store.id == 1L }

        assertThat(result is Result.Success)
        val store = (result as Result.Success).data

        assertThat(store.toPhysicalStoreDTO().storeEquals(expected)).isTrue()
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
