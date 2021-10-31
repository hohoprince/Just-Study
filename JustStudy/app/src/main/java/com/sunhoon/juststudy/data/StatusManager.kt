package com.sunhoon.juststudy.data

import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.myEnum.ProgressStatus
import com.sunhoon.juststudy.myEnum.TimeCountType

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

    var timeCountType: TimeCountType = TimeCountType.TIMER

    var studyTime: Long = 0

    var breakTime: Int = 0

    var remainTime: Long = 0

    /* 학습 진행 여부 */
    var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    /* 메시지 전송 허가 여부 */
    var isSendMessage = false
}