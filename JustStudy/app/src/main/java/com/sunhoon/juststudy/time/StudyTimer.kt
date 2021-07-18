package com.sunhoon.juststudy.time

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.enum.ProgressStatus
import com.sunhoon.juststudy.ui.study.StudyViewModel

class StudyTimer(millisInFuture: Long, countDownInterval: Long, private var timeText: MutableLiveData<String>,
                 private val viewModel: StudyViewModel
) : CountDownTimer(millisInFuture, countDownInterval) {

    private val statusManager = StatusManager.getInstance()

    override fun onTick(millisUntilFinished: Long) {
        val remainHours = "%02d".format(millisUntilFinished / 1000 / (60 * 60))
        val remainMinutes = "%02d".format((millisUntilFinished / 1000 % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(millisUntilFinished / 1000 % 60)

        timeText.value = "$remainHours:$remainMinutes:$remainSeconds"
    }

    override fun onFinish() {

        when (statusManager.progressStatus) {
            ProgressStatus.STUDYING -> {
                Log.i("MyInfo", "학습 끝")
                statusManager.progressStatus = ProgressStatus.RESTING
                viewModel.startBreakTimer()
            }
            ProgressStatus.RESTING -> {
                Log.i("MyInfo", "휴식 끝")
                statusManager.progressStatus = ProgressStatus.STUDYING
                viewModel.startStudyTimer()
            }
            else -> timeText.value = "Finish"
        }
        // 알람
    }
}