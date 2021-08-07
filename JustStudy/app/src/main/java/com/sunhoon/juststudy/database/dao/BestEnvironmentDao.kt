package com.sunhoon.juststudy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sunhoon.juststudy.database.entity.BestEnvironment

@Dao
interface BestEnvironmentDao {

    @Insert
    fun insert(bestEnvironment: BestEnvironment)

    @Update
    fun update(bestEnvironment: BestEnvironment)

    @Query("SELECT * FROM best_environment WHERE id = 1")
    fun read(): BestEnvironment?

    @Query("SELECT * FROM best_environment WHERE id = 1")
    fun readLiveData(): LiveData<BestEnvironment>
}