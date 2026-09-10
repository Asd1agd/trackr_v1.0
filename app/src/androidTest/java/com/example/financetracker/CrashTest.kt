package com.example.financetracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrashTest {
    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(2000)
    }
}
