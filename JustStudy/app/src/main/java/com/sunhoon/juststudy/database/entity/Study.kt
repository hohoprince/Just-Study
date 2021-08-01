package com.sunhoon.juststudy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "study")
class Study(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    var endTime: Long? = null,

) {
    override fun toString(): String {
        return "Study(id: $id, startTime: $startTime, endTime: $endTime)"
    }
}