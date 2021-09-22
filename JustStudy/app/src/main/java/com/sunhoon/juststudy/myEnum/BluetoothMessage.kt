package com.sunhoon.juststudy.myEnum

enum class BluetoothMessage(val value: String, val description: String) {

    LAMP_NONE("a","사용 안함"),
    LAMP_2700K("b", "2700k"),
    LAMP_4000K("c", "4000k"),
    LAMP_6500K("d", "6500k"),

    WHITE_NOISE_NONE("j", "사용 안함"),
    WHITE_NOISE_FIREWOOD("e", "장작"), // 장작
    WHITE_NOISE_MUSIC_1("f", "음악 1"), // 음악 1
    WHITE_NOISE_MUSIC_2("g", "음악 2"), // 음악 2
    WHITE_NOISE_RAIN("h", "빗 소리"), // 빗소리
    WHITE_NOISE_MUSIC_3("i", "음악 3"), // 빗소리

    HEIGHT_UP("U", "책상 높이 up"),
    HEIGHT_DOWN("D", "책상 높이 down"),
    HEIGHT_STOP("S", "책상 높이 조절 정지"),

    ANGLE_DOWN("X", "책받침 각도 아래로"),
    ANGLE_UP("Y", "책받침 각도 위로"),
    ANGLE_STOP("Z", "책받침 각도 조절 정지"),

    DESK_RESET("R", "책상 초기화"),
    DESK_RESTORATION("B", "책상 복구"),

    STUDY_START("p", "공부 시작"), // 공부를 시작할때
    STUDY_END("s", "공부 종료"), // 휴식이나 공부 끝날 때

    CLEAN("E", "지우개 가루 청소")

}