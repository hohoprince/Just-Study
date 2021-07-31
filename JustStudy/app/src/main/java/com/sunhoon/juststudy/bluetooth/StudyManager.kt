package com.sunhoon.juststudy.bluetooth

import android.content.Context
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.enum.Lamp
import com.sunhoon.juststudy.enum.WhiteNoise

class StudyManager(context: Context) {

    val appDatabase = AppDatabase.getDatabase(context)

    lateinit var currentWhiteNoise: WhiteNoise
    lateinit var currentLamp: Lamp

    companion object {
        @Volatile
        private var INSTANCE: StudyManager? = null

        fun getInstance(context: Context): StudyManager {
            return INSTANCE ?: synchronized(this) {
                val instance = StudyManager(context)
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