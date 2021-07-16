package com.sunhoon.juststudy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "study_detail")
data class StudyDetail(

    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "con_level")
    val conLevel: Int,

    @ColumnInfo(name = "start_time")
    val startTime: LocalDateTime,

    @ColumnInfo(name = "end_time")
    val endTime: LocalDateTime,

    @ColumnInfo(name = "angle_id")
    val angleId: Long,

    @ColumnInfo(name = "lamp_id")
    val lampId: Long,

    @ColumnInfo(name = "white_noise_id")
    val whiteNoiseId: Long,

    @ColumnInfo(name = "height_id")
    val heightId: Long,

    @ColumnInfo(name = "study_id")
    val studyId: Long

)

