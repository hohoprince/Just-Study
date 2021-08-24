package com.sunhoon.juststudy.myEnum

enum class BluetoothMessage(val value: String, val description: String) {

    // TODO: 이 부분은 회의해서 맞춰봐야함
    ANGLE_0("a","각도 0도"),
    ANGLE_15("b","각도 15도"),
    ANGLE_30("c", "각도 30도"),
    ANGLE_45("d", "각도 45도"),

    WHITE_NOISE_NONE("j", "사용 안함"),
    WHITE_NOISE_WAVE("e", "파도 소리"), // 장작
    WHITE_NOISE_WIND("f", "바람 소리"), // 음악 1
    WHITE_NOISE_LEAF("g", "나뭇잎 소리"), // 음악 2
    WHITE_NOISE_RAIN("h", "빗 소리"), // 빗소리 5번 음악 3 메시지: i

    LAMP_NONE("a","사용 안함"),
    LAMP_3500K("b", "3500k"), // 2700 6500 4000
    LAMP_5000K("d", "5000k"),
    LAMP_6500K("c", "6500k"),

    HEIGHT_UP("U", "책상 높이 up"),
    HEIGHT_DOWN("D", "책상 높이 down"),
    HEIGHT_STOP("S", "책상 높이 조절 정지"),
    // 리셋 추가 R
    // 복구 B
    // 책상 각도 뒤로 X
    // 앞으로 Y
    // 멈춤 Z
    // 키 ~170 1
    // 키 ~175 2
    // 키 ~180 3
    // 키 ~185 4
    // 키 ~190 5
    // 지우개 가루 청소 E

    STUDY_START("p", "공부 시작"), // 공부를 시작할때
    BREAK_TIME_START("r", "휴식 시작"), // 없애기
    STUDY_END("s", "공부 종료") // 휴식이나 공부 끝날 때

}