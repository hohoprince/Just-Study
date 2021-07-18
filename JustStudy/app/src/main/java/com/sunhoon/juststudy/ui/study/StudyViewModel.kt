package com.sunhoon.juststudy.ui.study

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.enum.ProgressStatus
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter
import java.time.LocalDateTime
import java.util.*

class StudyViewModel : ViewModel() {

    private val statusManager = StatusManager.getInstance()

    // 시간 텍스트
    private val _time = MutableLiveData<String>().apply {
        value = "00:00:00"
    }
    val time: LiveData<String> = _time

    // 현재 책상 각도
    private val _currentAngle = MutableLiveData<Int>().apply {
        value = 0
    }
    var currentAngle: LiveData<Int> = _currentAngle

    // 현재 램프 밝기
    private val _currentLight = MutableLiveData<Int>().apply {
        value = 0
    }
    var currentLight: LiveData<Int> = _currentLight

    // 현재 백색 소음
    private val _currentNoise = MutableLiveData<Int>().apply {
        value = 0
    }
    var currentNoise: LiveData<Int> = _currentNoise

    // 현재 집중도
    private val _currentConcentration = MutableLiveData<Int>().apply {
        value = 61
    }
    var currentConcentration: LiveData<Int> = _currentConcentration

    var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    var toastingMessage: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = null
    }


    // 타이머
    private lateinit var studyTimer : StudyTimer

    // 타이머의 남은 시간
    private var remainTime: Long = 0L


    // 공부 타이머 시작
    fun startStudyTimer() {
        // studyTimer = StudyTimer(remainTime, 1000, _time, this)
        // TODO: 테스트용 공부시간
        studyTimer = StudyTimer((10 * 1000).toLong(), 1000, _time, this)
        studyTimer.start()
        isPlaying.value = true
        statusManager.progressStatus = ProgressStatus.STUDYING
        toastingMessage.value = "공부 시작"
        Log.i("MyInfo", "공부 시작")
    }

    // 휴식 타이머 시작
    fun startBreakTimer() {
//        setUserTime((statusManager.breakTime * 60 * 1000).toLong())
//        studyTimer = StudyTimer(remainTime, 1000, _time, this)
        // TODO: 테스트용 휴식시간
        setUserTime((10 * 1000).toLong())
        studyTimer = StudyTimer((10 * 1000).toLong(), 1000, _time, this)
        studyTimer.start()
        statusManager.progressStatus = ProgressStatus.RESTING
        toastingMessage.value = "휴식 시작"
        Log.i("MyInfo", "휴식 시작")
    }

    // 타이머 종료
    fun stopTimer() {
        isPlaying.value = false
        studyTimer.cancel()
        statusManager.progressStatus = ProgressStatus.WAITING
        setUserTime(statusManager.studyTime)
        toastingMessage.value = "공부 종료"
        Log.i("MyInfo", "공부 종료, 타이머 종료")
    }

    // 사용자 설정 시간
    fun setUserTime(userTime: Long) {
        remainTime = userTime
        _time.value = TimeConverter.longToStringTime(userTime)
    }

    fun setCurrentLight(value: Int) {
        _currentLight.value = value
    }

    fun setCurrentNoise(value: Int) {
        _currentNoise.value = value
    }

    fun setCurrentAngle(value: Int) {
        _currentAngle.value = value
    }

}