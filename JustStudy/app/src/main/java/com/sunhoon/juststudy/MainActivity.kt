package com.sunhoon.juststudy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.dao.BestEnvironmentDao
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.StudyEnvironment
import com.sunhoon.juststudy.myEnum.WhiteNoise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    private var bluetoothSPP: BluetoothSPP = BluetoothSPP(this)

    private val studyManager: StudyManager = StudyManager.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_study, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        startBluetoothService() // 블루투스 서비스 시작

        studyManager.appDatabase = appDatabase
        studyManager.bluetoothSPP = bluetoothSPP

        bluetoothSPP.setOnDataReceivedListener { _, message ->
            Log.i("MyTag", "Received Message: $message")
            studyManager.process(message)
        }

        bluetoothSPP.setBluetoothConnectionListener(object: BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String?, address: String?) {
                Toast.makeText(this@MainActivity, "블루투스 연결: name = $name", Toast.LENGTH_SHORT).show()
                Log.i("MyTag", "bluetooth 연결: name = $name")
            }

            override fun onDeviceDisconnected() {
                Toast.makeText(this@MainActivity, "블루투스 연결 해제", Toast.LENGTH_SHORT).show()
                Log.i("MyTag", "bluetooth 연결 해제")
            }

            override fun onDeviceConnectionFailed() {
                Toast.makeText(this@MainActivity, "블루투스 연결 실패", Toast.LENGTH_LONG).show()
                Log.w("MyTag", "bluetooth 연결 실패")
            }

        })

        appDatabase.studyDetailDao().getAllOrderByDate().observe(this, Observer { studyDetails ->
            if (studyDetails.isNotEmpty()) {
                val lampScoreMap: MutableMap<Lamp, Int> = mutableMapOf()
                lampScoreMap[Lamp.LAMP_3500K] = 0
                lampScoreMap[Lamp.LAMP_5000K] = 0
                lampScoreMap[Lamp.LAMP_6500K] = 0
                lampScoreMap[Lamp.AUTO] = 0
                lampScoreMap[Lamp.NONE] = 0
                val whiteNoiseScoreMap: MutableMap<WhiteNoise, Int> = mutableMapOf()
                whiteNoiseScoreMap[WhiteNoise.RAIN] = 0
                whiteNoiseScoreMap[WhiteNoise.LEAF] = 0
                whiteNoiseScoreMap[WhiteNoise.WIND] = 0
                whiteNoiseScoreMap[WhiteNoise.WAVE] = 0
                whiteNoiseScoreMap[WhiteNoise.NONE] = 0
                whiteNoiseScoreMap[WhiteNoise.AUTO] = 0
                val angleScoreMap: MutableMap<Angle, Int> = mutableMapOf()
                angleScoreMap[Angle.DEGREE_0] = 0
                angleScoreMap[Angle.DEGREE_15] = 0
                angleScoreMap[Angle.DEGREE_30] = 0
                angleScoreMap[Angle.DEGREE_45] = 0
                angleScoreMap[Angle.AUTO] = 0

                studyDetails.forEach { studyDetail ->
                    val score = studyDetail.conLevel
                    lampScoreMap[Lamp.getByValue(studyDetail.lampId)] =
                        lampScoreMap[Lamp.getByValue(studyDetail.lampId)]!!.plus(score)
                    whiteNoiseScoreMap[WhiteNoise.getByValue(studyDetail.whiteNoiseId)] =
                        whiteNoiseScoreMap[WhiteNoise.getByValue(studyDetail.whiteNoiseId)]!!.plus(score)
                    angleScoreMap[Angle.getByValue(studyDetail.angleId)] =
                        angleScoreMap[Angle.getByValue(studyDetail.angleId)]!!.plus(score)
                }

                val bestLamp = getBestEnvironment(lampScoreMap, studyDetails.size)
                val bestAngle = getBestEnvironment(angleScoreMap, studyDetails.size)
                val bestWhiteNoise = getBestEnvironment(whiteNoiseScoreMap, studyDetails.size)
                Log.i("MyTag", "bestLamp: ${bestLamp.toString()}")
                Log.i("MyTag", "bestAngle: ${bestAngle.toString()}")
                Log.i("MyTag", "bestWhiteNoise: ${bestWhiteNoise.toString()}")
                val bestEnvironment = BestEnvironment(
                    bestAngle = bestAngle?.ordinal ?: Angle.DEGREE_0.ordinal,
                    bestLamp = bestLamp?.ordinal ?: Lamp.NONE.ordinal,
                    bestWhiteNoise = bestWhiteNoise?.ordinal ?: WhiteNoise.NONE.ordinal)
                GlobalScope.launch(Dispatchers.IO) {
                    val be: BestEnvironment? = appDatabase.bestEnvironmentDao().read()
                    if (be == null) {
                        appDatabase.bestEnvironmentDao().insert(bestEnvironment)
                        Log.i("MyTag", "insert bestEnvironment: $bestEnvironment")
                    } else {
                        appDatabase.bestEnvironmentDao().update(bestEnvironment)
                        Log.i("MyTag", "update bestEnvironment: $bestEnvironment")
                    }
                    studyManager.bestEnvironment = be
                }
            }
        })
    }

    private fun <T : StudyEnvironment<T>> getBestEnvironment(scoreMap: MutableMap<T, Int>, size: Int): T? {
        scoreMap.keys.forEach { key ->
            scoreMap[key] = scoreMap[key]!! / size
        }
        val maxScore = scoreMap.values.maxOrNull()
        maxScore?.let { maxScore ->
            scoreMap.keys.forEach { key ->
                if (scoreMap[key] == maxScore) {
                    return key
                }
            }
        }
        return null
    }

    /**
     * 블루투스 서비스
     */
    private fun startBluetoothService() {
        if (bluetoothSPP.isBluetoothEnabled) {
            bluetoothSPP.setupService()
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER)
            bluetoothSPP.pairedDeviceAddress.forEach { address ->
                Log.i("MyTag", "블루투스 기기 연결 시도")
                bluetoothSPP.connect(address)
            }
        } else {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기", Toast.LENGTH_LONG).show()
            Log.w("MyTag", "Bluetooth를 지원하지 않는 기기")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSPP.disconnect()
    }

}