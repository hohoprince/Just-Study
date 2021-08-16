package com.sunhoon.juststudy.myEnum

enum class BluetoothMessage(val value: String, val description: String) {

    ANGLE_0("a","각도 0도"),
    ANGLE_15("b","각도 15도"),
    ANGLE_30("c", "각도 30도"),
    ANGLE_45("d", "각도 45도"),

    WHITE_NOISE_NONE("e", "사용 안함"),
    WHITE_NOISE_WAVE("f", "파도 소리"),
    WHITE_NOISE_WIND("g", "바람 소리"),
    WHITE_NOISE_LEAF("h", "나뭇잎 소리"),
    WHITE_NOISE_RAIN("i", "빗 소리"),

    LAMP_NONE("j","사용 안함"),
    LAMP_3500K("k", "3500k"),
    LAMP_5000K("l", "5000k"),
    LAMP_6500K("m", "6500k"),

    HEIGHT_UP("n", "책상 높이 up"),
    HEIGHT_DOWN("o", "책상 높이 down"),
    HEIGHT_STOP("p", "책상 높이 조절 정지"),

    STUDY_START("q", "공부 시작"),
    BREAK_TIME_START("r", "휴식 시작"),
    STUDY_END("s", "공부 종료")

}