package com.sunhoon.juststudy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sunhoon.juststudy.database.dao.StudyDao
import com.sunhoon.juststudy.database.dao.StudyDetailDao
import com.sunhoon.juststudy.database.entity.Study
import com.sunhoon.juststudy.database.entity.StudyDetail

@Database(entities = [StudyDetail::class, Study::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDetailDao(): StudyDetailDao
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}