package com.sunhoon.juststudy.myEnum

enum class ConcentrationLevel(val description: String) {

    VERY_LOW("매우 낮음"),
    LOW("낮음"),
    NORMAL("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음");

    companion object {
        private val VALUES = Lamp.values()

        fun getByOrdinal(value: Int) = VALUES.first { it.ordinal == value }

        fun getByValue(concentrationLevel: Int): ConcentrationLevel {
            return when (concentrationLevel) {
                in 0..40 -> VERY_LOW
                in 40..54 -> LOW
                in 55..69 -> NORMAL
                in 70..85 -> HIGH
                in 86..100 -> VERY_HIGH
                else -> VERY_LOW
            }
        }
    }

}