package com.sunhoon.juststudy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sunhoon.juststudy.myEnum.Angle
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise
import java.util.*

@Entity(tableName = "study_detail")
class StudyDetail(

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

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

    override fun toString(): String {
        return "StudyDetail(집중도: $conLevel / 시간: ${Date(time)} / 책상 각도: ${Angle.getByValue(angleId)} " +
                "/ 책상 높이: $height / 램프: ${Lamp.getByValue(lampId)} / 백색소음: ${WhiteNoise.getByValue(whiteNoiseId)}" +
                " / studyId: $studyId)"
    }
}

