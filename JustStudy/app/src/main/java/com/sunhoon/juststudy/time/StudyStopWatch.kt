package com.sunhoon.juststudy.time

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudyStopWatch(private var timeText: MutableLiveData<String>) {

    var time: Long = 0L
    private var isRunning: Boolean = true

    fun start() {
        isRunning = true

        GlobalScope.launch {
            while (isRunning) {
                Thread.sleep(200)
                time += 200
                val remainHours = "%02d".format(time / 1000 / (60 * 60))
                val remainMinutes = "%02d".format((time / 1000 % (60 * 60)) / 60)
                val remainSeconds = "%02d".format(time / 1000 % 60)
                withContext(Dispatchers.Main) {
                    if (isRunning) {
                        timeText.value = "$remainHours:$remainMinutes:$remainSeconds"
                    }
                }
            }
        }

    }

    fun stop() {
        isRunning = false
        time = 0L
        timeText.value = "00:00:00"
    }
}