package com.sunhoon.juststudy.myEnum

enum class Lamp(description: String) {
    AUTO("자동"),
    NONE("사용 안함"),
    L_3500K("3500K"),
    L_5000K("5000K"),
    L_6500K("6500K")
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }

}