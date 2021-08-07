package com.sunhoon.juststudy.database.dao

import androidx.room.*
import com.sunhoon.juststudy.database.entity.Study

@Dao
interface StudyDao {

    @Insert
    fun insert(study: Study): Long

    @Delete
    fun delete(study: Study)

    @Update
    fun update(study: Study)

    @Query("SELECT * FROM study WHERE id=:id")
    fun readById(id: Long): Study

    @Query("DELETE FROM study")
    fun deleteAll()

}