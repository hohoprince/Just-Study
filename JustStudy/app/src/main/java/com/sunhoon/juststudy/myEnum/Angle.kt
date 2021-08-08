package com.sunhoon.juststudy.myEnum

enum class Angle(val description: String) : StudyEnvironment<Angle> {

    AUTO("자동"),
    DEGREE_0("0º"),
    DEGREE_15("15º"),
    DEGREE_30("30º"),
    DEGREE_45("45º")
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }

}