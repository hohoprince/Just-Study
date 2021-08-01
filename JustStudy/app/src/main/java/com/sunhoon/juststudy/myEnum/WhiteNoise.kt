package com.sunhoon.juststudy.myEnum

enum class WhiteNoise(description: String) {
    AUTO("자동"),
    NONE("사용 안함"),
    WAVE("파도 소리"),
    WIND("바람 소리"),
    LEAF("나뭇잎 소리"),
    RAIN("빗소리")
    ;

    companion object {
        private val VALUES = WhiteNoise.values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }
}