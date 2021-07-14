package com.sunhoon.juststudy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sunhoon.juststudy.database.entity.StudyDetail

@Dao
interface StudyDetailDao {

    @Query("SELECT * FROM study_detail")
    fun getAll(): List<StudyDetail>

    @Insert
    fun insert(studyDetail: StudyDetail)

    @Delete
    fun delete(studyDetail: StudyDetail)

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<StudyDetail>

//    @Query("SELECT * FROM StudyDetail WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): StudyDetail

//    @Insert
//    fun insertAll(vararg studyDetail: StudyDetail)

}