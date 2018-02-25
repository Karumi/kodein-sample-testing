package com.karumi.kodein.sample

import android.content.Intent
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Mock private lateinit var applicationScopeClass: ApplicationScopeClass
    @Mock private lateinit var activityScopeClass: ActivityScopeClass

    @Rule
    @JvmField
    val testRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java, true, false)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.resetInjection()
        app.overrideModule = testDependencies
    }

    @Test
    fun shouldShowInjectedNameProvidedByTheDomainWhenReplaceByAMock() {
        whenever(applicationScopeClass.getText()).thenReturn("Mock Name")
        startActivity()

        onView(withText("Mock Name")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowInjectedNameInActivityWhenReplaceByAMock() {
        whenever(applicationScopeClass.getText()).thenReturn("Mock Activity Scope")
        startActivity()

        onView(withText("Mock Activity Scope")).check(matches(isDisplayed()))
    }

    fun startActivity(args: Bundle = Bundle()): MainActivity {
        val intent = Intent()
        intent.putExtras(args)
        return testRule.launchActivity(intent)
    }

    val testDependencies = Module(allowSilentOverride = true) {
        bind<ApplicationScopeClass>() with instance(applicationScopeClass)
        bind<ActivityScopeClass>() with instance(activityScopeClass)
    }
}