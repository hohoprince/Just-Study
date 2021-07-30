package com.sunhoon.juststudy

import android.bluetooth.BluetoothAdapter
import android.content.Intent
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


class MainActivity : AppCompatActivity() {

    var bluetoothSPP: BluetoothSPP = BluetoothSPP(this)


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

        bluetoothSPP.setOnDataReceivedListener { _, message ->
            Log.i("MyInfo", "Received Message: $message")
        }

    }

    override fun onStart() {
        super.onStart()
        if (bluetoothSPP.isBluetoothEnabled) {
            bluetoothSPP.setupService()
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER)
            bluetoothSPP.pairedDeviceAddress.forEach { address ->
                bluetoothSPP.connect(address)
                Log.i("MyInfo", "bluetooth 기기 연결: address = $address")
            }
        } else {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기", Toast.LENGTH_LONG).show()
        }
    }

}