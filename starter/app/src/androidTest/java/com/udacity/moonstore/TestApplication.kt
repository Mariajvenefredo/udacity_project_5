package com.udacity.moonstore

import android.app.Application
import com.udacity.moonstore.data.dto.PhysicalStoreDTO
import com.udacity.moonstore.data.dto.StoreItemDTO
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.list.StoreListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val dataSource = FakeDataSource()

        val myModule = module {

            viewModel {
                StoreListViewModel(
                    this@TestApplication,
                    dataSource
                )
            }
            viewModel {
                StoreViewModel(
                    this@TestApplication,
                    dataSource
                )
            }
        }

        GlobalContext.startKoin {
            modules(listOf(myModule))
        }
    }
}
