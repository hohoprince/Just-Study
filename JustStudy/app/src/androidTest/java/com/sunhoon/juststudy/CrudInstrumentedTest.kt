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

//        val tempMap: MutableMap<String, MutableList<Int>> = mutableMapOf()
//        val statisticsMap: MutableMap<String, Int> = mutableMapOf()
//
//        val studyDetails: List<StudyDetail> = db.studyDetailDao().getAllOrderByDate()
//        studyDetails.forEach {
//            val date = Calendar.getInstance()
//            date.time = it.time
//            // val strDate = String.format("%d.%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.DAY_OF_MONTH]) // 일
//            val strDate = String.format("%d.%d", date[Calendar.YEAR], date[Calendar.MONTH] + 1) // 월
////            val strDate = String.format("%d.%d.%d주", date[Calendar.YEAR], date[Calendar.MONTH] + 1, date[Calendar.WEEK_OF_MONTH]) // 주
//            if (tempMap.contains(strDate)) {
//                tempMap[strDate]?.add(it.conLevel)
//            } else {
//                tempMap[strDate] = mutableListOf()
//            }
//        }
//        tempMap.forEach { (strDate, conLevelList) ->
//            val conLevelOfAvg = conLevelList.average().roundToInt()
//            statisticsMap[strDate] = conLevelOfAvg
//        }
//        println(tempMap)
//        println(statisticsMap)
//        val keyList = statisticsMap.keys.sorted().reversed()
//            .subList(0, min(7, statisticsMap.keys.size)).reversed()
//        keyList.forEach {
//            println("$it: ${statisticsMap[it]}")
//        }
//        println()

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