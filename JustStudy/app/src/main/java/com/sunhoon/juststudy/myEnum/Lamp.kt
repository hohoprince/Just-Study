package com.sunhoon.juststudy.myEnum

enum class Lamp(description: String) {
    AUTO("자동"),
    NONE("사용 안함"),
    LAMP_3500K("3500K"),
    LAMP_5000K("5000K"),
    LAMP_6500K("6500K")
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }

}