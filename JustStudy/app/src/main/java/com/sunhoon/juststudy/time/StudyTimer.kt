package com.sunhoon.juststudy.time

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.enum.ProgressStatus
import com.sunhoon.juststudy.ui.study.StudyViewModel

class StudyTimer(millisInFuture: Long, countDownInterval: Long, private var timeText: MutableLiveData<String>,
                 private val viewModel: StudyViewModel
) : CountDownTimer(millisInFuture, countDownInterval) {

    private val statusManager = StatusManager.getInstance()

    override fun onTick(millisUntilFinished: Long) {
        val remainTotal = millisUntilFinished / 1000
        val remainHours = "%02d".format(remainTotal / (60 * 60))
        val remainMinutes = "%02d".format((remainTotal % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(remainTotal % 60)

        timeText.value = "$remainHours:$remainMinutes:$remainSeconds"
    }

    override fun onFinish() {

        when (statusManager.progressStatus) {
            ProgressStatus.STUDYING -> {
                timeText.value = "공부 끝!"
                statusManager.progressStatus = ProgressStatus.RESTING
                viewModel.isPlaying.value = false
            }
            ProgressStatus.RESTING -> {
                timeText.value = "휴식 끝!"
                statusManager.progressStatus = ProgressStatus.WAITING
                viewModel.isPlaying.value = false
            }
            else -> timeText.value = "Finish~"
        }
        // 알람
    }
}