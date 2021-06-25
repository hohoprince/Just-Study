package com.sunhoon.juststudy.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _stringConcentrationTime = MutableLiveData<String>().apply {
        value = "0분"
    }
    val stringConcentrationTime: LiveData<String> = _stringConcentrationTime

    // 휴식 시간 설정값
    val breakTime = MutableLiveData<Int>().apply {
        value = 0
    }


    fun setStringConTime(time: String) {
        _stringConcentrationTime.value = time
    }

}