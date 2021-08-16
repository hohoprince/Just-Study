package com.sunhoon.juststudy.ui.study

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.myEnum.*
import com.sunhoon.juststudy.time.StudyTimer
import com.sunhoon.juststudy.time.TimeConverter

class StudyViewModel(application: Application): AndroidViewModel(application) {

    private val appDatabase: AppDatabase = AppDatabase.getDatabase(application)

    private val statusManager = StatusManager.getInstance()

    val studyManager = StudyManager.getInstance()

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

    /* 학습 진행 여부 */
    var isPlaying: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    /* 토스트 메시지를 전달 */
    var toastingMessage: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = null
    }


    // 타이머
    private lateinit var studyTimer : StudyTimer

    // 타이머의 남은 시간
    private var remainTime: Long = 0L

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
     * 현재 책상 각도 정보를 세팅
     */
    fun setCurrentAngle(value: Angle) {
        studyManager.currentAngle.value = value
    }

    /**
     * 책상 각도 변경 메시지를 보낸다
     */
    fun sendChangeAngleMessage(angle: Angle) {
        when (angle) {
            Angle.AUTO -> {
                studyManager.bestEnvironment?.bestAngle?.let {
                    sendChangeAngleMessage(Angle.getByValue(it))
                }
            }
            Angle.DEGREE_0 -> studyManager.writeMessage(BluetoothMessage.ANGLE_0)
            Angle.DEGREE_15 -> studyManager.writeMessage(BluetoothMessage.ANGLE_15)
            Angle.DEGREE_30 -> studyManager.writeMessage(BluetoothMessage.ANGLE_30)
            Angle.DEGREE_45 -> studyManager.writeMessage(BluetoothMessage.ANGLE_45)
        }
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
            WhiteNoise.WAVE -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_WAVE)
            WhiteNoise.WIND -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_WIND)
            WhiteNoise.LEAF -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_LEAF)
            WhiteNoise.RAIN -> studyManager.writeMessage(BluetoothMessage.WHITE_NOISE_RAIN)
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
            Lamp.LAMP_3500K -> studyManager.writeMessage(BluetoothMessage.LAMP_3500K)
            Lamp.LAMP_5000K -> studyManager.writeMessage(BluetoothMessage.LAMP_5000K)
            Lamp.LAMP_6500K -> studyManager.writeMessage(BluetoothMessage.LAMP_6500K)
        }
    }

    /**
     * 블루투스 메시지 전송 테스트용
     */
    fun sendMessageForTest(message: BluetoothMessage) {
        studyManager.writeMessage(message)
    }

}