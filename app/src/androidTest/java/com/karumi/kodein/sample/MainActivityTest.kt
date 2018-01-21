package com.karumi.kodein.sample

import android.content.Intent
import android.os.Bundle
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val testRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java, true, false)

    @Test
    fun shouldShowInjectedNameInTheActivityWhenReplaceByAMock() {
        startActivity()

        onView(withText("Hello World!")).check(matches(isDisplayed()))
    }

    fun startActivity(args: Bundle = Bundle()): MainActivity {
        val intent = Intent()
        intent.putExtras(args)
        return testRule.launchActivity(intent)
    }
}