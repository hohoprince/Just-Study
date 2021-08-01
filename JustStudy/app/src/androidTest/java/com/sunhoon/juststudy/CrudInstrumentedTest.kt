package com.sunhoon.juststudy

import android.util.Log
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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

//        val mock = StudyDetail(1L, 60, Date().time, Date().time,
//            1L, 1L, 1L, 1L, 1L)
//
//        db.studyDetailDao().insert(mock)

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