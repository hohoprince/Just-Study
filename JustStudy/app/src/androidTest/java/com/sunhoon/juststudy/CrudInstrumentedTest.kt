package com.sunhoon.juststudy

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail

import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Integer.min

import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CrudInstrumentedTest {

    @Test
    fun insertTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val db = AppDatabase.getDatabase(appContext)

    }

    @Test
    fun readAllTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val db = AppDatabase.getDatabase(appContext)

        val studyDetails = db.studyDetailDao().getAll()
        println("hi")
        studyDetails.forEach { _ ->
            println(studyDetails)
        }
    }
}