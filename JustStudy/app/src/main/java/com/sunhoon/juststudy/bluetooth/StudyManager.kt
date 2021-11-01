package com.sunhoon.juststudy.bluetooth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.database.entity.Study
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.myEnum.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.math.max

class StudyManager {

    lateinit var appDatabase: AppDatabase

    lateinit var bluetoothSPP: BluetoothSPP

    lateinit var bluetoothSPP2: BluetoothSPP

    private val statusManager: StatusManager = StatusManager.getInstance()

    private var groupId: Long = 0L

    var bestEnvironment: BestEnvironment? = null

    var lampRankingList: List<Lamp> = mutableListOf()

    var whiteNoiseRankingList: List<WhiteNoise> = mutableListOf()

    var minConcentration: MutableLiveData<ConcentrationLevel> = MutableLiveData<ConcentrationLevel>().apply {
        value = ConcentrationLevel.VERY_LOW
    }

    private var onRestListener: OnRestListener? = null

    private var selectionIndex = 1


    /* 현재 책상 높이 */
    val currentHeight = MutableLiveData<Int>().apply {
        value = 0
    }

    /* 현재 램프 밝기 */
    val currentLamp = MutableLiveData<Lamp>().apply {
        value = Lamp.NONE
    }

    /* 현재 백색 소음 */
    val currentWhiteNoise = MutableLiveData<WhiteNoise>().apply {
        value = WhiteNoise.NONE
    }

    /* 현재 집중도 */
    val currentConcentration = MutableLiveData<Int>().apply {
        value = 0
    }

    /* 집중도 오차를 맞추기 위한 기회 횟수 */
    private var lowConcentrationCount = 0

    /* 환경 변경 횟수 */
    var environmentChangeCount = 0


    companion object {
        @Volatile
        private var INSTANCE: StudyManager? = null

        fun getInstance(): StudyManager {
            return INSTANCE ?: synchronized(this) {
                val instance = StudyManager()
                INSTANCE = instance
                instance
            }
        }
    }

    fun setOnRestListener(listener: OnRestListener) {
        this.onRestListener = listener
    }

    fun resetCount() {
        lowConcentrationCount = 0
        environmentChangeCount = 0
        selectionIndex = 1
    }

    fun createStudy() {
        GlobalScope.launch(Dispatchers.IO) {
            groupId = appDatabase.studyDao().insert(Study(startTime = Date()))
        }
    }

    fun updateStudy() {
        GlobalScope.launch(Dispatchers.IO) {
            val study = appDatabase.studyDao().readById(groupId)
            study.endTime = Date()
            appDatabase.studyDao().update(study)
        }
    }

    /**
     * 전달받은 메시지를 처리한다
     */
    fun process(msg: String) {
        if (statusManager.progressStatus.value == ProgressStatus.STUDYING) {
            try {
                val changed: Int = msg.toInt()
                val score: Int = max(0, 100 - 3 * changed) // 집중도 점수
                Log.i("MyTag", "현재 집중도: $score")
                currentConcentration.value = score
                insertStudyDetail(score) // 집중도를 받은 시각의 데이터 삽입
                ConcentrationLevel.getByValue(score).ordinal
                if (ConcentrationLevel.getByValue(score).ordinal < minConcentration.value?.ordinal!!) { // 현재 집중도 < 최소 집중도
                    lowConcentrationCount += 1
                    Log.i("MyTag", "lowConcentrationCount increase: $lowConcentrationCount")
                    if (lowConcentrationCount >= 10) { // 연속 10번 집중도가 좋지 않을 때 환경 변경
                        environmentChangeCount += 1
                        lowConcentrationCount = 0
                        Log.i("MyTag", "environmentChangeCount increase: $environmentChangeCount")

                        // 환경 변경이 3회 일어나면 휴식을 권유
                        if (environmentChangeCount >= 3 && statusManager.timeCountType == TimeCountType.TIMER) {
                            Log.i("MyTag", "휴식 권유")
                            environmentChangeCount = 0
                            onRestListener?.onRest()
                            return
                        }

                        GlobalScope.launch {
                            val index = selectionIndex
                            if (currentLamp.value == Lamp.AUTO) {
                                when (lampRankingList[index % lampRankingList.size]) {
                                    Lamp.NONE -> sendMessage(BluetoothMessage.LAMP_NONE)
                                    Lamp.LAMP_2700K -> sendMessage(BluetoothMessage.LAMP_2700K)
                                    Lamp.LAMP_4000K -> sendMessage(BluetoothMessage.LAMP_4000K)
                                    Lamp.LAMP_6500K -> sendMessage(BluetoothMessage.LAMP_6500K)
                                    else -> Log.w("MyTag", "잘못된 램프 밝기")
                                }
                                delay(1000)
                            }

                            if (currentWhiteNoise.value == WhiteNoise.AUTO) {
                                when (whiteNoiseRankingList[index % whiteNoiseRankingList.size]) {
                                    WhiteNoise.NONE -> sendMessage(BluetoothMessage.WHITE_NOISE_NONE)
                                    WhiteNoise.RAIN -> sendMessage(BluetoothMessage.WHITE_NOISE_RAIN)
                                    WhiteNoise.FIREWOOD -> sendMessage(BluetoothMessage.WHITE_NOISE_FIREWOOD)
                                    WhiteNoise.MUSIC_1 -> sendMessage(BluetoothMessage.WHITE_NOISE_MUSIC_1)
                                    WhiteNoise.MUSIC_2 -> sendMessage(BluetoothMessage.WHITE_NOISE_MUSIC_2)
                                    WhiteNoise.MUSIC_3 -> sendMessage(BluetoothMessage.WHITE_NOISE_MUSIC_3)
                                    else -> Log.w("MyTag", "잘못된 백색 소음")
                                }
                            }
                        }

                        selectionIndex += 1
                    }
                } else { // 한 번이라도 조건에 만족하지 못 하면 초기화
                    Log.i("MyTag", "lowConcentrationCount Reset")
                    lowConcentrationCount = 0
                }
            } catch (e: Exception) {
                Log.e("MyTag", "메시지를 처리할 수 없음" + e.stackTraceToString())
            }
        }
    }

    /**
     * db에 학습 환경 정보를 저장한다
     */
    private fun insertStudyDetail(score: Int) {
        val whiteNoiseId = if (currentWhiteNoise.value!! == WhiteNoise.AUTO) {
            bestEnvironment?.bestWhiteNoise!!
        } else {
            currentWhiteNoise.value!!.ordinal
        }
        val lampId = if (currentLamp.value!! == Lamp.AUTO) {
            bestEnvironment?.bestLamp!!
        } else {
            currentLamp.value!!.ordinal
        }
        GlobalScope.launch(Dispatchers.IO) {

            val studyDetail = StudyDetail(
                conLevel = score, time = Date(),
                height = currentHeight.value!!,
                lampId = lampId,
                whiteNoiseId = whiteNoiseId,
                studyId = groupId)
            appDatabase.studyDetailDao().insert(studyDetail)
        }
    }

    /**
     * 책상에 메시지를 전송한다
     */
    fun sendMessage(msg: BluetoothMessage) {
        if (statusManager.isSendMessage) {
            sendMessageWithNoCondition(msg)
        }
    }

    /**
     * 조건 없이 책상에 메시지를 전송한다
     */
    fun sendMessageWithNoCondition(msg: BluetoothMessage) {
        if (msg == BluetoothMessage.STUDY_END_PULSE || msg == BluetoothMessage.STUDY_START) {
            bluetoothSPP2.send(msg.value, false)
            Log.i("MyTag", "spp2: Send Message: ${msg.value}(${msg.description})")
        } else {
            bluetoothSPP.send(msg.value, false)
            Log.i("MyTag", "spp1: Send Message: ${msg.value}(${msg.description})")
        }
    }


    interface OnRestListener {
        fun onRest()
    }

    /**
     * TODO: 테스트 완료되면 삭제
     * 정해진 집중도를 이용해 동작을 제어한다.
     */
    fun useTestScore() {
        val scores = listOf(
            30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
            30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
            30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
            1, 30, 2, 15, 1, 33, 1, 7, 16, 2,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        )
        GlobalScope.launch {
            scores.forEach {
                delay(1000)
                withContext(Dispatchers.Main) {
                    process(it.toString())
                }
            }
        }
    }

}