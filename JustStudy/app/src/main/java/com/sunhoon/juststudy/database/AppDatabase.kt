package com.sunhoon.juststudy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sunhoon.juststudy.database.dao.StudyDetailDao
import com.sunhoon.juststudy.database.entity.StudyDetail

@Database(entities = [StudyDetail::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDetailDao(): StudyDetailDao

}