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

    fun startTimer(millis: Long) {
        studyTimer = StudyTimer(millis, 1000, _time)
        studyTimer.start()
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text

}