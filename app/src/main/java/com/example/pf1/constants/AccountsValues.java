package com.example.pf1.constants;

public class AccountsValues {
    // 性別
    public static final String GENDER_MALE = "M";    // 男性
    public static final String GENDER_FEMALE = "F";  // 女性
    public static final String GENDER_OTHER = "O";   // その他

    // ステータス（DB項目定義書の「アクセス許可/アクセス禁止」に対応）
    public static final int STATUS_ACTIVE = 0;    // アクセス許可（アクティブ）
    public static final int STATUS_INACTIVE = 1;  // アクセス禁止（非アクティブ）

    // 権限
    public static final int PERMISSIONS_ADMIN = 0;    // 管理者権限
    public static final int PERMISSIONS_GENERAL = 1;  // 一般権限

    // 削除フラグ
    public static final int IS_DELETED_FALSE = 0;  // 削除フラグOFF
    public static final int IS_DELETED_TRUE = 1;   // 削除フラグON
}