package com.sunhoon.juststudy.ui.home

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.sunhoon.juststudy.bluetooth.StudyManager
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.database.entity.StudyDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    val dataSet: LiveData<List<StudyDetail>> = db.studyDetailDao().getAllOrderByDate()

    val bestEnvironment: LiveData<BestEnvironment> = db.bestEnvironmentDao().readLiveData()
}
