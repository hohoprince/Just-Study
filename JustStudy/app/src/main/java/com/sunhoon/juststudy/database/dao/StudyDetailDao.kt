package com.sunhoon.juststudy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sunhoon.juststudy.database.entity.StudyDetail
import java.util.*

@Dao
interface StudyDetailDao {

    @Query("SELECT * FROM study_detail")
    fun getAll(): List<StudyDetail>

    @Query("SELECT * FROM study_detail ORDER BY time")
    fun getAllOrderByDate(): LiveData<List<StudyDetail>>

    @Insert
    fun insert(studyDetail: StudyDetail)

    @Delete
    fun delete(studyDetail: StudyDetail)

    @Insert
    fun insertAll(studyDetails: List<StudyDetail>)

    @Query("DELETE FROM study_detail")
    fun deleteAll()

}