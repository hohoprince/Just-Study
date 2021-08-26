package com.sunhoon.juststudy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.myEnum.Lamp
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
                val lampScoreListMap: MutableMap<Lamp, MutableList<Int>> = mutableMapOf()
                lampScoreListMap[Lamp.LAMP_2700K] = mutableListOf()
                lampScoreListMap[Lamp.LAMP_4000K] = mutableListOf()
                lampScoreListMap[Lamp.LAMP_6500K] = mutableListOf()
                lampScoreListMap[Lamp.NONE] = mutableListOf()
                val whiteNoiseScoreListMap: MutableMap<WhiteNoise, MutableList<Int>> = mutableMapOf()
                whiteNoiseScoreListMap[WhiteNoise.RAIN] = mutableListOf()
                whiteNoiseScoreListMap[WhiteNoise.MUSIC_2] = mutableListOf()
                whiteNoiseScoreListMap[WhiteNoise.MUSIC_1] = mutableListOf()
                whiteNoiseScoreListMap[WhiteNoise.FIREWOOD] = mutableListOf()
                whiteNoiseScoreListMap[WhiteNoise.MUSIC_3] = mutableListOf()
                whiteNoiseScoreListMap[WhiteNoise.NONE] = mutableListOf()

                studyDetails.forEach { studyDetail ->
                    val score = studyDetail.conLevel
                    lampScoreListMap[Lamp.getByValue(studyDetail.lampId)]!!.add(score)
                    whiteNoiseScoreListMap[WhiteNoise.getByValue(studyDetail.whiteNoiseId)]!!.add(score)
                }

                val lampScoreRankList = convertToScoreRankList(lampScoreListMap)
                val whiteNoiseScoreRankList = convertToScoreRankList(whiteNoiseScoreListMap)
                studyManager.lampRankingList = lampScoreRankList
                studyManager.whiteNoiseRankingList = whiteNoiseScoreRankList

                Log.d("MyTag", "lamp scoreList: $lampScoreRankList")
                Log.d("MyTag", "whiteNoise scoreList: $whiteNoiseScoreRankList")

                val bestEnvironment = BestEnvironment(
                    bestLamp = lampScoreRankList[0].ordinal,
                    bestWhiteNoise = whiteNoiseScoreRankList[0].ordinal)

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

    /**
     * scoreListMap 을 ScoreRankList 로 변환
     */
    private fun <T> convertToScoreRankList(scoreListMap: MutableMap<T, MutableList<Int>>): List<T> {
        return scoreListMap.keys.map { Pair(it, scoreListMap[it]?.average()?.toInt()!!)
        }.sortedByDescending { it.second }.map { it.first }
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
            Log.w("MyTag", "Bluetooth 지원하지 않는 기기")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSPP.disconnect()
    }

}