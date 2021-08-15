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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max

class StudyManager {

    lateinit var appDatabase: AppDatabase
    lateinit var bluetoothSPP: BluetoothSPP
    private val statusManager: StatusManager = StatusManager.getInstance()
    private var groupId: Long = 0L
    var bestEnvironment: BestEnvironment? = null
    var angleRankingList: List<Angle> = mutableListOf()
    var lampRankingList: List<Lamp> = mutableListOf()
    var whiteNoiseRankingList: List<WhiteNoise> = mutableListOf()

    /* 현재 책상 각도 */
    val currentAngle = MutableLiveData<Angle>().apply {
        value = Angle.DEGREE_0
    }

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

    fun createStudy() {
        GlobalScope.launch(Dispatchers.IO) {
            groupId = appDatabase.studyDao().insert(Study(startTime = Date()))
            Log.i("MyTag", "Study 생성")
        }
    }

    fun updateStudy() {
        GlobalScope.launch(Dispatchers.IO) {
            val study = appDatabase.studyDao().readById(groupId)
            study.endTime = Date()
            appDatabase.studyDao().update(study)
            Log.i("MyTag", "Study 업데이트")
        }
    }

    /**
     * 전달받은 메시지를 처리한다
     */
    fun process(msg: String) {
        if (statusManager.progressStatus == ProgressStatus.STUDYING) {
            val changed: Int = msg.toInt()
            val score: Int = max(0, 100 - 3 * changed) // 집중도 점수
            Log.i("MyTag", "현재 집중도: $score")
            currentConcentration.value = score

            insertStudyDetail(score) // 집중도를 받은 시각의 데이터 삽입
        }
    }

    /**
     * db에 학습 환경 정보를 저장한다
     */
    private fun insertStudyDetail(score: Int) {
        val angleId = if (currentAngle.value!! == Angle.AUTO) {
            bestEnvironment?.bestAngle!!
        } else {
            currentAngle.value!!.ordinal
        }
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
                angleId = angleId,
                height = currentHeight.value!!,
                lampId = lampId,
                whiteNoiseId = whiteNoiseId,
                studyId = groupId)
            appDatabase.studyDetailDao().insert(studyDetail)
            Log.i("MyTag", "studyDetail 삽입: $studyDetail")
        }
    }

    /**
     * 책상에 메시지를 전송한다
     */
    fun writeMessage(msg: BluetoothMessage) {
        bluetoothSPP.send(msg.value, true)
        Log.i("MyTag", "Send Message: ${msg.value}(${msg.description})")
    }



}