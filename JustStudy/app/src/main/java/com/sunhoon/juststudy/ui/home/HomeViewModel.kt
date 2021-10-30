package com.sunhoon.juststudy.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sunhoon.juststudy.database.AppDatabase
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.database.entity.StudyDetail

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    val dataSet: LiveData<List<StudyDetail>> = db.studyDetailDao().getAllOrderByDate()

    val bestEnvironment: LiveData<BestEnvironment> = db.bestEnvironmentDao().readLiveData()
}
