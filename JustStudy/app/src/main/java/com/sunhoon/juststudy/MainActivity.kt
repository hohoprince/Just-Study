package com.sunhoon.juststudy

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    private var bluetoothSPP1: BluetoothSPP = BluetoothSPP(this)
    private var bluetoothSPP2: BluetoothSPP = BluetoothSPP(this)

    private val studyManager: StudyManager = StudyManager.getInstance()

    private var deviceCount = 0


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
        studyManager.bluetoothSPP = bluetoothSPP1
        studyManager.bluetoothSPP2 = bluetoothSPP2



        bluetoothSPP1.setOnDataReceivedListener { _, message ->
            Log.i("MyTag", "Received Message: $message")
            studyManager.process(message)
        }
        bluetoothSPP2.setOnDataReceivedListener { _, message ->
            Log.i("MyTag", "Received Message: $message")
            studyManager.process(message)
        }

        val bluetoothConnectionListener = object: BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String?, address: String?) {
                Toast.makeText(this@MainActivity,
                    "블루투스 연결: name = $name address = $address", Toast.LENGTH_SHORT).show()
                Log.i("MyTag", "bluetooth 연결: name = $name")
                deviceCount += 1

                // FIXME: 연결할 블루투스 장치 개수만큼 수정
                if (deviceCount == 1) {
                    val dlg = Dialog(this@MainActivity)
                    dlg.setContentView(R.layout.dialog_connected)
                    dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dlg.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    dlg.setCancelable(false)
                    dlg.show()
                    GlobalScope.launch {
                        delay(1500)
                        withContext(Dispatchers.Main) {
                            dlg.dismiss()
                        }
                    }
                }
            }

            override fun onDeviceDisconnected() {
                Toast.makeText(this@MainActivity, "블루투스 연결 해제", Toast.LENGTH_SHORT).show()
                Log.i("MyTag", "bluetooth 연결 해제")
                deviceCount -= 1
            }

            override fun onDeviceConnectionFailed() {
                Toast.makeText(this@MainActivity, "블루투스 연결 실패", Toast.LENGTH_LONG).show()
                Log.w("MyTag", "bluetooth 연결 실패")
            }

        }

        bluetoothSPP1.setBluetoothConnectionListener(bluetoothConnectionListener)
        bluetoothSPP2.setBluetoothConnectionListener(bluetoothConnectionListener)

        appDatabase.studyDetailDao().getAllOrderByDate().observe(this) { studyDetails ->
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

                val bestEnvironment = BestEnvironment(
                    bestLamp = lampScoreRankList[0].ordinal,
                    bestWhiteNoise = whiteNoiseScoreRankList[0].ordinal
                )

                GlobalScope.launch(Dispatchers.IO) {
                    val be: BestEnvironment? = appDatabase.bestEnvironmentDao().read()
                    if (be == null) {
                        appDatabase.bestEnvironmentDao().insert(bestEnvironment)
                    } else {
                        appDatabase.bestEnvironmentDao().update(bestEnvironment)
                    }
                    studyManager.bestEnvironment = be
                }
            }
        }
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
        if (bluetoothSPP1.isBluetoothEnabled && bluetoothSPP2.isBluetoothEnabled) {
            bluetoothSPP1.setupService()
            bluetoothSPP2.setupService()
            bluetoothSPP1.startService(BluetoothState.DEVICE_OTHER)
            bluetoothSPP2.startService(BluetoothState.DEVICE_OTHER)
            bluetoothSPP1.pairedDeviceAddress.forEach { address ->
                if (address == "98:D3:41:FD:5A:01") {
                    Log.i("MyTag", "spp1: 블루투스 기기 연결 시도 $address")
                    bluetoothSPP1.connect(address)
                }
            }
            bluetoothSPP2.pairedDeviceAddress.forEach { address ->
                // FIXME: 심박수 센서 MAC 주소 변경
                val btMacAddress = "98:D3:91:FD:B9:84" // BT1
//                val btMacAddress = "98:D3:31:FD:80:02"
                if (address == btMacAddress) {
                    Log.i("MyTag", "spp2: 블루투스 기기 연결 시도 $address")
                    bluetoothSPP2.connect(address)
                }
            }
        } else {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기", Toast.LENGTH_LONG).show()
            Log.w("MyTag", "Bluetooth 지원하지 않는 기기")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSPP1.disconnect()
        bluetoothSPP2.disconnect()
    }

}