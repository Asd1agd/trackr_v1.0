package com.example.financetracker

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CrashTest {
    @Test
    fun testActivityLaunch() {
        try {
            Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
