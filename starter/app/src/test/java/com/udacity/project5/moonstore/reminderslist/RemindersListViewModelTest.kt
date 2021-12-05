/*
package com.udacity.project5.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project5.locationreminders.MainCoroutineRule
import com.udacity.project5.locationreminders.data.FakeDataSource
import com.udacity.project5.locationreminders.storeitemslist.RemindersListViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource
    private lateinit var context: Application

    private val reminderList = mutableListOf(
        FavoriteItemDTO("Title1", "Description1"),
        FavoriteItemDTO("Title2", "Description2")
    )

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource(reminderList)
        context = ApplicationProvider.getApplicationContext()

        remindersListViewModel = RemindersListViewModel(context, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_LoadsAllReminders_listIsSame() {
        remindersListViewModel.loadReminders()
        val addedList = remindersListViewModel.remindersList.value
        if (addedList != null) {
            assertEquals(addedList[0].id, reminderList[0].id)
            assertEquals(addedList[0].title, reminderList[0].title)
            assertEquals(addedList[0].description, reminderList[0].description)
            assertEquals(addedList[0].location, reminderList[0].location)
            assertEquals(addedList[0].latitude, reminderList[0].latitude)
            assertEquals(addedList[0].longitude, reminderList[0].longitude)

            assertEquals(addedList[1].id, reminderList[1].id)
            assertEquals(addedList[1].title, reminderList[1].title)
            assertEquals(addedList[1].description, reminderList[1].description)
            assertEquals(addedList[1].location, reminderList[1].location)
            assertEquals(addedList[1].latitude, reminderList[1].latitude)
            assertEquals(addedList[1].longitude, reminderList[1].longitude)
        }
    }
    @Test
    fun loadReminders_Loading() {
        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.showLoading.value, true)

        mainCoroutineRule.resumeDispatcher()

        assertEquals(remindersListViewModel.showLoading.value, false)
    }

    @Test
    fun loadReminders_LoadsAllReminders_listIsNotNull() {
        remindersListViewModel.loadReminders()
        val addedList = remindersListViewModel.remindersList.value
        assertNotNull(addedList)

        assertNull(remindersListViewModel.showSnackBar.value)
        assertEquals(remindersListViewModel.showNoData.value, false)
    }

    @Test
    fun loadReminders_ErrorOnLoading() {
        dataSource.shouldReturnError = true
        remindersListViewModel.loadReminders()
        val addedList = remindersListViewModel.remindersList.value
        assertNull(addedList)
        assertNotNull(remindersListViewModel.showSnackBar.value)
        assertEquals(remindersListViewModel.showNoData.value, true)
    }
}*/
