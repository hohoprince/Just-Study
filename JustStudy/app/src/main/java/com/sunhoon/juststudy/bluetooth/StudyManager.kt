package com.sunhoon.juststudy.bluetooth

import androidx.lifecycle.MutableLiveData
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max

class StudyManager {

    lateinit var appDatabase: AppDatabase
    lateinit var bluetoothSPP: BluetoothSPP
    var groupId: Long = 0L

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


    fun process(msg: String) {
        val changed: Int = msg.toInt()
        val score: Int = max(0, 100 - 3 * changed) // 집중도 점수

        insertStudyDetail(score) // 집중도를 받은 시각의 데이터 삽입

    }

    /**
     * insert StudyDetail
     */
    private fun insertStudyDetail(score: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.studyDetailDao().insert(
                StudyDetail(conLevel = score, time = Date().time, angleId = currentAngle.value!!.ordinal,
                    height = currentHeight.value!!, lampId = currentLamp.value!!.ordinal,
                    whiteNoiseId = currentWhiteNoise.value!!.ordinal, studyId = 1L)
            )
        }
    }

}