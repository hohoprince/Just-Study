package com.sunhoon.juststudy.myEnum

enum class Angle(description: String) {

    AUTO("자동"),
    DEGREE_0("0도"),
    DEGREE_15("15도"),
    DEGREE_30("30도"),
    DEGREE_45("45도")
    ;

    companion object {
        private val VALUES = Angle.values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }

}