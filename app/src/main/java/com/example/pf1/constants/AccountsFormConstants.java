package com.example.pf1.constants;

import java.util.List;

public class AccountsFormConstants {
    public static final int USERNAME_MAX_LENGTH = 255;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 32;
    public static final int FURIGANA_MAX_LENGTH = 255;
    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int AGE_MIN = 0;
    public static final int AGE_MAX = 999;
    public static final int PROFILE_MAX_LENGTH = 1500;

    // 性別の選択肢 [コード, 表示名]
    public static final List<String[]> GENDER_LIST = List.of(
        new String[]{AccountsValues.GENDER_MALE, "男性"},
        new String[]{AccountsValues.GENDER_FEMALE, "女性"},
        new String[]{AccountsValues.GENDER_OTHER, "その他"}
    );
}