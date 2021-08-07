package com.sunhoon.juststudy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sunhoon.juststudy.myEnum.Lamp
import com.sunhoon.juststudy.myEnum.WhiteNoise

@Entity(tableName = "best_environment")
class BestEnvironment(
    @PrimaryKey
    val id: Long = 1L,

    @ColumnInfo(name = "best_angle")
    val bestAngle: Int,

    @ColumnInfo(name = "best_white_noise")
    val bestWhiteNoise: Int,

    @ColumnInfo(name = "best_lamp")
    val bestLamp: Int
) {

}