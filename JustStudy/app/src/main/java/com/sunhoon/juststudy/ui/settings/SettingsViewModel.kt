package com.sunhoon.juststudy.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val appDatabase: AppDatabase = AppDatabase.getDatabase(application)

    private val _stringConcentrationTime = MutableLiveData<String>().apply {
        value = "0분"
    }
    val stringConcentrationTime: LiveData<String> = _stringConcentrationTime

    // 휴식 시간 설정값
    val breakTime = MutableLiveData<Int>().apply {
        value = 0
    }

    // 시작 화면 설정 값
    var startScreen = MutableLiveData<Int>().apply {
        value = 0
    }


    fun setStringConTime(time: String) {
        _stringConcentrationTime.value = time
    }

    /**
     * 테스트용 데이터 생성
     */
    fun createTestData() {
        val studyDetails = ArrayList<StudyDetail>()
        val numOfStudy = 100
        val startTimestamp = 1619823600 // 5월 1일
        val endTimestamp = 1628439505// 8월 8일

        val random = Random()

        for (i in 0 until numOfStudy) {
            studyDetails.add(
                StudyDetail(
                    conLevel = random.nextInt(51) + 50, // 50 ~ 100
                    time = Date(((random.nextInt(endTimestamp - startTimestamp) + startTimestamp).toLong()) * 1000),
                    height = random.nextInt(10000),
                    lampId = random.nextInt(4) + 1,
                    whiteNoiseId = random.nextInt(6) + 1,
                    studyId = (random.nextInt(numOfStudy) + 1).toLong()
                )
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.studyDetailDao().insertAll(studyDetails)
        }
    }

    /**
     * 테스트 데이터 삭제
     */
    fun deleteTestData() {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.studyDetailDao().deleteAll()
            appDatabase.studyDao().deleteAll()
            appDatabase.bestEnvironmentDao().deleteAll()
        }
    }

}