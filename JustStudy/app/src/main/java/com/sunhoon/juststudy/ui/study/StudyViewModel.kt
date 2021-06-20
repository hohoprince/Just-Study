package com.sunhoon.juststudy.ui.study

import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.time.StudyTimer

class StudyViewModel : ViewModel() {

    private val _time = MutableLiveData<String>().apply {
        value = "00:00:00"
    }
    val time: LiveData<String> = _time
    private lateinit var studyTimer : StudyTimer
    private var remainTime: Long = 0L


    fun startTimer() {
        studyTimer = StudyTimer(remainTime, 1000, _time)
        studyTimer.start()
    }

    fun setUserTime(userTime: Long) {
        remainTime = userTime
        val remainTotal = userTime / 1000
        val remainHours = "%02d".format(remainTotal / (60 * 60))
        val remainMinutes = "%02d".format((remainTotal % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(remainTotal % 60)

        _time.value = "$remainHours:$remainMinutes:$remainSeconds"
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text

}