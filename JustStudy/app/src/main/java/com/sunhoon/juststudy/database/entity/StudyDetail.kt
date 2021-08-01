package com.sunhoon.juststudy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "study_detail")
class StudyDetail(

    @ColumnInfo(name = "con_level")
    val conLevel: Int,

    @ColumnInfo(name = "time")
    val time: Long,

    @ColumnInfo(name = "angle_id")
    val angleId: Int,

    @ColumnInfo(name = "height")
    val height: Int,

    @ColumnInfo(name = "lamp_id")
    val lampId: Int,

    @ColumnInfo(name = "white_noise_id")
    val whiteNoiseId: Int,

    @ColumnInfo(name = "study_id")
    val studyId: Long
    ) {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}

