package com.sunhoon.juststudy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.database.AppDatabase


class MainActivity : AppCompatActivity() {

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

        studyManager.appDatabase = AppDatabase.getDatabase(this)
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