package com.sunhoon.juststudy.myEnum

enum class BreakTime(val description: String, val time: Long) {

    MINUTE_5("5분", 5 * 60 * 1000),
    MINUTE_10("10분", 10 * 60 * 1000),
    MINUTE_15("15분", 15 * 60 * 1000),
    MINUTE_20("20분", 20 * 60 * 1000),
    MINUTE_25("25분", 25 * 60 * 1000),
    MINUTE_30("30분", 30 * 60 * 1000),
    ;

    companion object {
        private val VALUES = BreakTime.values()

        fun getByOrdinal(value: Int) = VALUES.first { it.ordinal == value }

        fun getTimeByOrdinal(value: Int): Long {
            return getByOrdinal(value).time
        }
    }

}