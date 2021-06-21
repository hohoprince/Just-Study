package com.sunhoon.juststudy.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _stringConcentrationTime = MutableLiveData<String>().apply {
        value = "0분"
    }
    val stringConcentrationTime: LiveData<String> = _stringConcentrationTime

    var breakTime: Long = 0L

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is notifications Fragment"
//    }
//    val text: LiveData<String> = _text

    fun setStringConTime(time: String) {
        _stringConcentrationTime.value = time
    }

}