package com.udacity.moonstore.settings

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.udacity.moonstore.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@MediumTest
class SettingsFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun closeKoin() {
        stopKoin()
    }

    @Test
    fun fragmentIsShowing_settingsAppears() {
        launchFragmentInContainer<SettingsFragment>(
            Bundle(), R.style.AppTheme
        )

        Espresso.onView(ViewMatchers.withId(R.id.settingsItem))
            .check(ViewAssertions.matches(isDisplayed()))
            .check(ViewAssertions.matches(withText(R.string.settings_stock_description)))

        Espresso.onView(ViewMatchers.withId(R.id.imageNotificationExplanation))
            .check(ViewAssertions.matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.textNotificationExplanation))
            .check(ViewAssertions.matches(isDisplayed()))
            .check(ViewAssertions.matches(withText(R.string.settings_notification_long_explanation)))
    }
}