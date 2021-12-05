package com.udacity.moonstore

import android.app.Application
import com.udacity.project5.moonstore.storeitemslist.StoreListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

class TestApplication : Application() {
    private val reminderList = mutableListOf(
        FavoriteItemDTO("Title1", "Description1", "Home", 0.01, 0.01),
        FavoriteItemDTO("Title2", "Description2", "Work", 0.02, 0.02)
    )

    override fun onCreate() {
        super.onCreate()
        val dataSource = FakeDataSource(reminderList)

        val myModule = module {
            viewModel {
                StoreListViewModel(
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