package com.sunhoon.juststudy.myEnum

enum class Lamp(val description: String) : StudyEnvironment<Lamp> {
    AUTO("자동"),
    NONE("사용 안함"),
    LAMP_2700K("2700K"),
    LAMP_4000K("4000K"),
    LAMP_6500K("6500K")
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }

}