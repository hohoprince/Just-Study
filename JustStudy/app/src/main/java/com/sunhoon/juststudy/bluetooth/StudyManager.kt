package com.sunhoon.juststudy.bluetooth

import androidx.lifecycle.MutableLiveData
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise

class StudyManager {

    lateinit var appDatabase: AppDatabase
    lateinit var bluetoothSPP: BluetoothSPP

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
        val changed = msg.toInt()
        val score = 100 - 3 * changed

    }

}