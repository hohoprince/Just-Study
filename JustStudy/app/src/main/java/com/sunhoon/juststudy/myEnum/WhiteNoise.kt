package com.sunhoon.juststudy.myEnum

enum class WhiteNoise(val description: String) : StudyEnvironment<WhiteNoise> {
    AUTO("자동"),
    NONE("사용 안함"),
    FIREWOOD("장작 타는 소리"),
    MUSIC_1("음악1"),
    MUSIC_2("음악2"),
    RAIN("빗소리"),
    MUSIC_3("음악3")
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.first { it.ordinal == value }
    }
}