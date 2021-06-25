package com.sunhoon.juststudy.ui.study

import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter

class StudyViewModel : ViewModel() {

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


    // 타이머
    private lateinit var studyTimer : StudyTimer

    // 타이머의 남은 시간
    private var remainTime: Long = 0L




    // 타이머 시작
    fun startTimer() {
        studyTimer = StudyTimer(remainTime, 1000, _time)
        studyTimer.start()
    }

    // 타이머 종료
    fun stopTimer() {
        studyTimer.cancel()
        studyTimer.onFinish()
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

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text

}