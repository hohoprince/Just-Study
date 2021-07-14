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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CrudInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "database-name"
        ).build()

        val mock = StudyDetail(1L, 60, LocalDateTime.now(), LocalDateTime.now(),
            1L, 1L, 1L, 1L, 1L)

        db.studyDetailDao().insert(mock)

        val list: List<StudyDetail> = db.studyDetailDao().getAll()

        list.forEach { l -> Log.d("testtest", l.toString()) }

    }
}