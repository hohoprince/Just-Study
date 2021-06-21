package com.sunhoon.juststudy.time

object TimeConverter {

    fun dayMinuteToLong(hourOfDay: Int, minute: Int): Long {
        return (hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000).toLong()
    }

    fun dayMinuteToStringMinute(hourOfDay: Int, minute: Int): String {
        return "${hourOfDay * 60 + minute}분"
    }

    fun longToStringTime(time: Long): String {
        val remainTotal = time / 1000
        val remainHours = "%02d".format(remainTotal / (60 * 60))
        val remainMinutes = "%02d".format((remainTotal % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(remainTotal % 60)

        return "$remainHours:$remainMinutes:$remainSeconds"
    }

}