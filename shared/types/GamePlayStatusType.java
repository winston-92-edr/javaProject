package com.mynet.shared.types;

public enum GamePlayStatusType {
    VALID(0, "VALID", "VALID"),
    WRONG_ROOM_ID(19, "Yanlış Oda Numarası", ""),
    ROOM_FULL(20, "Odada Yer Yok!", ""),
    USER_HAS_ROOM_ALREADY(21, "Zaten Bir Odadasınız!", ""),
    NOT_ENOUGH_MONEY(28, "Yeterli ML'en Yok", "%s odasında oynamak için en az %d ML'ye sahip olman gerekir"),
    NOT_VIP(32, "VIP olmaniz gerekli", ""),
    GENERAL_ERROR(40, "Hata Oluştu, Tekrar Deneyin", ""),
    MAINTENANCE_MODE(49, "Bakım Çalışması sebebiyle yeni oyuna başlayamazsınız", ""),
    FAST_PLAY_MODE_DISABLED(50, "Bu mod şu anda aktif değil.", ""),
    USER_HAS_TABLE_ALREADY(21, "Zaten Bir Masadasınız!", ""),
    TOURNAMENT_MODE_DISABLED(51, "Bu mod şu anda aktif değil.", ""),
    TOURNAMENT_NOT_AVAILABLE(52, "Turnuva bulunamadı", ""),
    NOT_ENOUGH_TICKET(53, "Yeterli Biletin Yok", "%s turnuvasında oynamak için %d Bilete sahip olman gerekir"),
    USER_TOURNAMENT_NOT_AVAILABLE(54, "Turnuvaya giriş yapmanız gerekmektedir", ""),
    USER_ALREADY_IN_TOURNAMENT(55, "Zaten turnuvadasınız", ""),
    TOURNAMENT_NOT_ACTIVE(56,"Bu turnuva aktif değildir","");

    private final int value;
    private final String title;
    private final String msg;

    GamePlayStatusType(int value, String title, String msg) {
        this.value = value;
        this.title = title;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }
}
