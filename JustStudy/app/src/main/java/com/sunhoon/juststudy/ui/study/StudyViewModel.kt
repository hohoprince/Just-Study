package com.sunhoon.juststudy.ui.study

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    /* 현재 책상 높이 */
    var currentHeight: LiveData<Int> = studyManager.currentHeight

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
        studyTimer = StudyTimer((10 * 1000).toLong(), 100,
            (10 * 1000).toLong(), _time, this)
        studyTimer.setOnExtendTimeListener(object: StudyTimer.OnExtendTimeListener {
            override fun onTime() {
                // 집중도가 80 이상일 때 학습 시간을 연장
                if (currentConcentration.value!! >= 80) {
                    val newTime = statusManager.remainTime + 5000
                    studyTimer.cancel()
                    studyTimer = StudyTimer(newTime, 100, newTime,
                        _time, this@StudyViewModel)
                    studyTimer.start()
                }
                /**
                 * TODO: 이 부분에 휴식을 권유하는 조건과 로직 추가
                 * 휴식을 권유하는 경우: 환경 변경을 3번 해도 집중도가 최소보다 낮을 때
                 * 환경 변경: 현재 집중도가 최소 집중도보다 낮으면 환경을 변경할 수 있도록
                 * 환경 변경 순서: ??? / 백색 소음, 램프, (책상 각도)
                 * 환경 변경은 80%가 지나지 않아도 변경할 수 있게
                 * 80%가 지나면 변경 횟수가 3번이 넘었는지 계속 체크해서 넘을시 휴식을 권유
                 * 집중도 체크는 10번의 입력이 최소보다 낮으면? 1번으로??
                 * 위의 연장 시간 연장 기준 집중도는 수정할 가능성이 있음
                 */
            }
        })
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
        studyTimer = StudyTimer((10 * 1000).toLong(),100, (10 * 1000).toLong()
            , _time, this)
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
        statusManager.remainTime = userTime
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
     * 블루투스 메시지 전송 테스트용
     */
    fun sendMessageForTest(message: BluetoothMessage) {
        studyManager.writeMessage(message)
    }

}