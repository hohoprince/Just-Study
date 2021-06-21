package com.sunhoon.juststudy.ui.study

import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter

class StudyViewModel : ViewModel() {

    private val _time = MutableLiveData<String>().apply {
        value = "00:00:00"
    }
    val time: LiveData<String> = _time
    private lateinit var studyTimer : StudyTimer
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

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text

}