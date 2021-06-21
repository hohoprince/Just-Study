package com.sunhoon.juststudy.time

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData

class StudyTimer(millisInFuture: Long, countDownInterval: Long, private var timeText: MutableLiveData<String>) : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        val remainTotal = millisUntilFinished / 1000
        val remainHours = "%02d".format(remainTotal / (60 * 60))
        val remainMinutes = "%02d".format((remainTotal % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(remainTotal % 60)

        timeText.value = "$remainHours:$remainMinutes:$remainSeconds"
    }

    override fun onFinish() {
        timeText.value = "Finish!"
    }
}