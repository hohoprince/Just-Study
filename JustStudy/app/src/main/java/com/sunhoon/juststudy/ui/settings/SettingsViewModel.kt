package com.sunhoon.juststudy.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.myEnum.ConcentrationLevel
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

    // 최소 집중도 설정 값
    var minConcentration = MutableLiveData<Int>().apply {
        value = 0
    }

    private val studyManager = StudyManager.getInstance()

    fun setStringConTime(time: String) {
        _stringConcentrationTime.value = time
    }

    fun setMinConcentration(minConcentration: ConcentrationLevel) {
        studyManager.minConcentration.value = minConcentration
    }

}