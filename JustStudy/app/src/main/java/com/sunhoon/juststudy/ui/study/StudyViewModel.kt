package com.sunhoon.juststudy.ui.study

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.*
import com.sunhoon.juststudy.time.StudyStopWatch
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter

class StudyViewModel(application: Application): AndroidViewModel(application) {

    private val statusManager = StatusManager.getInstance()

    val studyManager = StudyManager.getInstance()

    /* 시간 */
    private val _time = MutableLiveData<String>().apply {
        value = "00:00:00"
    }
    val time: LiveData<String> = _time

    /* 현재 램프 밝기 */
    var currentLamp: LiveData<Lamp> = studyManager.currentLamp

    /* 현재 백색 소음 */
    var currentWhiteNoise: LiveData<WhiteNoise> = studyManager.currentWhiteNoise

    /* 현재 집중도 */
    var currentConcentration: LiveData<Int> = studyManager.currentConcentration.apply { value = 0 }

    /* 학습 진행 여부 */
    var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    /* 토스트 메시지를 전달 */
    var toastingMessage: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = null
    }

    /* 타이머 */
    private lateinit var studyTimer : StudyTimer

    /* 스톱 워치 */
    private lateinit var studyStopWatch: StudyStopWatch


    /**
     * 새로운 Study 생성
     */
    fun createStudy() {
        studyManager.createStudy()
    }

    /**
     * Study의 상태를 업데이트
     */
    fun updateStudy() {
        studyManager.updateStudy()
    }

    // 공부 타이머 시작
    fun startStudyTimer() {
//         studyTimer = StudyTimer(statusManager.remainTime, 1000,
//             statusManager.remainTime, _time, this)
        // TODO: 테스트용 공부시간
        studyTimer = StudyTimer((10 * 1000).toLong(), 100,
            (10 * 1000).toLong(), _time, this)
        studyTimer.setOnExtendTimeListener(object: StudyTimer.OnExtendTimeListener {
            override fun onTime() { // 학습 80% 이상 진행
                // 집중도가 80 이상일 때 학습 시간을 연장
                if (currentConcentration.value!! >= 80) {
                    // FIXME: 10초(10_000)에서 10분(600_000)으로 변경
                    val newTime = statusManager.remainTime + 10_000
                    // val newTime = statusManager.remainTime + 600_000
                    studyTimer.cancel()
                    studyTimer = StudyTimer(newTime, 100, newTime,
                        _time, this@StudyViewModel)
                    studyTimer.start()
                }
            }
        })
        studyTimer.start()
        isPlaying.value = true
        statusManager.progressStatus.value = ProgressStatus.STUDYING
        toastingMessage.value = "공부 시작"
        studyManager.writeMessage(BluetoothMessage.STUDY_START)
        Log.i("MyTag", "공부 시작")
    }

    // 휴식 타이머 시작
    fun startBreakTimer() {
        studyTimer.cancel()
        Log.d("MyTag", "breakTime: ${BreakTime.getTimeByOrdinal(statusManager.breakTime)}");
        setUserTime(BreakTime.getTimeByOrdinal(statusManager.breakTime))
        studyTimer = StudyTimer(BreakTime.getTimeByOrdinal(statusManager.breakTime), 100,
            BreakTime.getTimeByOrdinal(statusManager.breakTime), _time, this)
        // TODO: 테스트용 휴식시간
//        setUserTime((10 * 1000).toLong())
//        studyTimer = StudyTimer((10 * 1000).toLong(),100, (10 * 1000).toLong()
//            , _time, this)
        studyTimer.start()
        statusManager.progressStatus.value = ProgressStatus.RESTING
        toastingMessage.value = "휴식 시작"
        Log.i("MyTag", "휴식 시작")
    }

    // 스탑워치 시작
    fun startStopWatch() {
        studyStopWatch = StudyStopWatch(_time)
        studyStopWatch.start()
        isPlaying.value = true
        statusManager.progressStatus.value = ProgressStatus.STUDYING
        toastingMessage.value = "공부 시작"
        studyManager.writeMessage(BluetoothMessage.STUDY_START)
        Log.i("MyTag", "공부 시작")
    }

    // 타이머 종료
    fun stopTimer() {
        studyTimer.cancel()
        statusManager.progressStatus.value = ProgressStatus.WAITING
        setUserTime(statusManager.studyTime)
        toastingMessage.value = "공부 종료"
        studyManager.writeMessage(BluetoothMessage.STUDY_END)
        Log.i("MyTag", "공부 종료, 타이머 종료")
    }

    fun stopStopWatch() {
        studyStopWatch.stop()
        statusManager.progressStatus.value = ProgressStatus.WAITING
        setUserTime(0)
        toastingMessage.value = "공부 종료"
        studyManager.writeMessage(BluetoothMessage.STUDY_END)
        studyManager.resetCount()
        Log.i("MyTag", "공부 종료, 타이머 종료")
    }

    fun resetDesk() {
        studyManager.writeMessage(BluetoothMessage.DESK_RESET);
    }

    // 사용자 설정 시간
    fun setUserTime(userTime: Long) {
        statusManager.remainTime = userTime
        _time.value = TimeConverter.longToStringTime(userTime)
    }

    fun rest() {
        studyTimer.finish()
    }

    /**
     * 현재 램프 정보를 세팅
     */
    fun setCurrentLamp(value: Lamp) {
        studyManager.currentLamp.value = value
    }

    /**
     * 현재 백색 소음 정보를 세팅
     */
    fun setCurrentWhiteNoise(value: WhiteNoise) {
        studyManager.currentWhiteNoise.value = value
    }

    /**
     * 책상 높이 변경 메시지를 보낸다
     */
    fun sendChangeHeightMessage(height: Height) {
        when (height) {
            Height.UP -> studyManager.writeMessage(BluetoothMessage.HEIGHT_UP)
            Height.DOWN -> studyManager.writeMessage(BluetoothMessage.HEIGHT_DOWN)
            Height.STOP -> studyManager.writeMessage(BluetoothMessage.HEIGHT_STOP)
        }
    }

    /**
     * 책받침 각도 변경 메시지를 보낸다
     */
    fun sendChangeAngleMessage(angle: Angle) {
        when (angle) {
            Angle.UP -> studyManager.writeMessage(BluetoothMessage.ANGLE_UP)
            Angle.DOWN -> studyManager.writeMessage(BluetoothMessage.ANGLE_DOWN)
            Angle.STOP -> studyManager.writeMessage(BluetoothMessage.ANGLE_STOP)
        }
    }

    /**
     * 백색 소음 변경 메시지를 보낸다
     */
    fun sendChangeWhiteNoiseMessage(whiteNoise: WhiteNoise) {
        when (whiteNoise) {
            WhiteNoise.AUTO -> {
                studyManager.bestEnvironment?.bestWhiteNoise?.let {
                    sendChangeWhiteNoiseMessage(WhiteNoise.getByValue(it))
                }
            }
            WhiteNoise.NONE -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_NONE)
            WhiteNoise.FIREWOOD -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_FIREWOOD)
            WhiteNoise.MUSIC_1 -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_MUSIC_1)
            WhiteNoise.MUSIC_2 -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_MUSIC_2)
            WhiteNoise.RAIN -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_RAIN)
            WhiteNoise.MUSIC_3 -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_MUSIC_3)
        }
    }

    /**
     * 램프 밝기 변경 메시지를 보낸다
     */
    fun sendChangeLampMessage(lamp: Lamp) {
        when (lamp) {
            Lamp.AUTO -> {
                studyManager.bestEnvironment?.bestLamp?.let {
                    sendChangeLampMessage(Lamp.getByValue(it))
                }
            }
            Lamp.NONE -> studyManager.writeMessage(BluetoothMessage.LAMP_NONE)
            Lamp.LAMP_2700K -> studyManager.writeMessage(BluetoothMessage.LAMP_2700K)
            Lamp.LAMP_4000K -> studyManager.writeMessage(BluetoothMessage.LAMP_4000K)
            Lamp.LAMP_6500K -> studyManager.writeMessage(BluetoothMessage.LAMP_6500K)
        }
    }

    /**
     * 지우개 가루 청소 메시지를 보낸다
     */
    fun sendCleanMessage() {
        studyManager.writeMessage(BluetoothMessage.CLEAN)
    }

    /**
     * 블루투스 메시지 전송 테스트용
     */
    fun sendMessageForTest(message: BluetoothMessage) {
        studyManager.writeMessage(message)
    }

}