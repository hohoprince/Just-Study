package com.sunhoon.juststudy.ui.study

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.ProgressStatus
import com.sunhoon.juststudy.myEnum.WhiteNoise
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter

class StudyViewModel : ViewModel() {

    private val statusManager = StatusManager.getInstance()

    private val studyManager = StudyManager.getInstance()

    /* 시간 */
    private val _time = MutableLiveData<String>().apply {
        value = "00:00:00"
    }
    val time: LiveData<String> = _time

    /* 현재 책상 각도 */
    var currentAngle: LiveData<Angle> = studyManager.currentAngle

    /* 현재 책상 높이 */
    var currentHeight: LiveData<Int> = studyManager.currentHeight

    /* 현재 램프 밝기 */
    var currentLamp: LiveData<Lamp> = studyManager.currentLamp

    /* 현재 백색 소음 */
    var currentWhiteNoise: LiveData<WhiteNoise> = studyManager.currentWhiteNoise

    /* 현재 집중도 */
    var currentConcentration: LiveData<Int> = studyManager.currentConcentration


    var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    var toastingMessage: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = null
    }


    // 타이머
    private lateinit var studyTimer : StudyTimer

    // 타이머의 남은 시간
    private var remainTime: Long = 0L


    // 공부 타이머 시작
    fun startStudyTimer() {
        // studyTimer = StudyTimer(remainTime, 1000, _time, this)
        // TODO: 테스트용 공부시간
        studyTimer = StudyTimer((10 * 1000).toLong(), 1000, _time, this)
        studyTimer.start()
        isPlaying.value = true
        statusManager.progressStatus = ProgressStatus.STUDYING
        toastingMessage.value = "공부 시작"
        Log.i("MyTag", "공부 시작")
    }

    // 휴식 타이머 시작
    fun startBreakTimer() {
//        setUserTime((statusManager.breakTime * 60 * 1000).toLong())
//        studyTimer = StudyTimer(remainTime, 1000, _time, this)
        // TODO: 테스트용 휴식시간
        setUserTime((10 * 1000).toLong())
        studyTimer = StudyTimer((10 * 1000).toLong(), 1000, _time, this)
        studyTimer.start()
        statusManager.progressStatus = ProgressStatus.RESTING
        toastingMessage.value = "휴식 시작"
        Log.i("MyTag", "휴식 시작")
    }

    // 타이머 종료
    fun stopTimer() {
        isPlaying.value = false
        studyTimer.cancel()
        statusManager.progressStatus = ProgressStatus.WAITING
        setUserTime(statusManager.studyTime)
        toastingMessage.value = "공부 종료"
        Log.i("MyTag", "공부 종료, 타이머 종료")
    }

    // 사용자 설정 시간
    fun setUserTime(userTime: Long) {
        remainTime = userTime
        _time.value = TimeConverter.longToStringTime(userTime)
    }

    fun setCurrentLamp(value: Lamp) {
        studyManager.currentLamp.value = value
    }

    fun setCurrentWhiteNoise(value: WhiteNoise) {
        studyManager.currentWhiteNoise.value = value
    }

    fun setCurrentAngle(value: Angle) {
        studyManager.currentAngle.value = value
    }

}