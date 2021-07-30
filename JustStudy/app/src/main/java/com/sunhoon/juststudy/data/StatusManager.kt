package com.sunhoon.juststudy.data

import com.sunhoon.juststudy.enum.ProgressStatus

class StatusManager {

    companion object {

        private val instance = StatusManager()

        @JvmStatic
        fun getInstance(): StatusManager {
            return instance
        }
    }

    var progressStatus = ProgressStatus.WAITING

    var studyTime: Long = 0
    var breakTime: Int = 0

    var isBluetoothConnected: Boolean = false

}