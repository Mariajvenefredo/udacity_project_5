package com.udacity.moonstore

import android.app.Application
import com.udacity.moonstore.data.StoreDataSource
import com.udacity.moonstore.data.local.StoreDatabase
import com.udacity.moonstore.data.local.StoreRepository
import com.udacity.moonstore.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.moonstore.storeItems.StoreViewModel
import com.udacity.moonstore.storeItems.list.StoreListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            single { StoreDatabase.getInstance(this@MyApp).onlineStoreDao() }
            single { StoreRepository(get()) } bind StoreDataSource::class

            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                StoreListViewModel(
                    this@MyApp,
                    get()
                )
            }
            viewModel {
                StoreViewModel(
                    this@MyApp,
                    get()
                )
            }

            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    this@MyApp,
                   // get()
                )
            }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}