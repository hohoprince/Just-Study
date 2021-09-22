package com.sunhoon.juststudy.data

import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.myEnum.ProgressStatus

class StatusManager {

    companion object {

        private val instance = StatusManager()

        @JvmStatic
        fun getInstance(): StatusManager {
            return instance
        }
    }

    var progressStatus: MutableLiveData<ProgressStatus> = MutableLiveData<ProgressStatus>().apply {
        value = ProgressStatus.WAITING
    }

    var studyTime: Long = 0
    var breakTime: Int = 0
    var remainTime: Long = 0
}