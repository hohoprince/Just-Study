package com.sunhoon.juststudy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sunhoon.juststudy.database.dao.BestEnvironmentDao
import com.sunhoon.juststudy.database.dao.StudyDao
import com.sunhoon.juststudy.database.dao.StudyDetailDao
import com.sunhoon.juststudy.database.entity.BestEnvironment
import com.sunhoon.juststudy.database.entity.Study
import com.sunhoon.juststudy.database.entity.StudyDetail
import com.sunhoon.juststudy.time.Converters

@Database(entities = [StudyDetail::class, Study::class, BestEnvironment::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDetailDao(): StudyDetailDao
    abstract fun studyDao(): StudyDao
    abstract fun bestEnvironmentDao(): BestEnvironmentDao

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