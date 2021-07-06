package com.sunhoon.juststudy.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPref {

    companion object {
        @JvmStatic
        fun getSharedPref(activity: Activity): SharedPreferences {
            return activity.getSharedPreferences("shared", Context.MODE_PRIVATE)
        }
    }

}