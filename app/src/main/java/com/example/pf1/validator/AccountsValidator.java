package com.example.pf1.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.pf1.constants.AccountsFormConstants;
import com.example.pf1.constants.AccountsLabel;
import com.example.pf1.messages.ErrorMessages;

@Component
public class AccountsValidator {

    // 指定された文字列が最大文字数以内かを検証する
    public String validateStrLength(String value, int max, String label) {
        if (value != null && value.length() > max) {
            return String.format(ErrorMessages.ERROR_MAX_LENGTH, label, max);
        }
        return null;
    }

    // ふりがなの形式が有効かを検証する（ひらがな＋長音符のみ）
    public String validateFurigana(String value) {
        String lengthError = validateStrLength(value, AccountsFormConstants.FURIGANA_MAX_LENGTH, AccountsLabel.FURIGANA_LABEL);
        if (lengthError != null) return lengthError;
        if (value != null && !value.isEmpty() && !Pattern.matches("[ぁ-んー]+", value)) {
            return ErrorMessages.ERROR_HIRAGANA_ONLY;
        }
        return null;
    }

    // 性別コードが M/F/O のいずれかかを検証する
    public String validateGender(String value) {
        if (value != null && !value.isEmpty() && !value.matches("[MFO]")) {
            return ErrorMessages.ERROR_GENDER_CODE;
        }
        return null;
    }

    // プロフィール画像のファイルサイズが 2MB 以下かを検証する
    public String validateProfileImageSize(long size) {
        if (size > 2 * 1024 * 1024) {
            return ErrorMessages.ERROR_IMAGE_SIZE;
        }
        return null;
    }

    // パスワードが要件を満たすかを検証する
    public String validatePassword(String value) {
        if (value == null || value.isEmpty()) {
            return String.format(ErrorMessages.ERROR_REQUIRED, AccountsLabel.PASSWORD_LABEL);
        }
        int len = value.length();
        if (len < AccountsFormConstants.PASSWORD_MIN_LENGTH || len > AccountsFormConstants.PASSWORD_MAX_LENGTH) {
            return String.format(ErrorMessages.ERROR_INVALID_RANGE, AccountsLabel.PASSWORD_LABEL,
                    AccountsFormConstants.PASSWORD_MIN_LENGTH, AccountsFormConstants.PASSWORD_MAX_LENGTH);
        }
        if (!value.matches("^[a-zA-Z0-9_-]+$")) {
            return ErrorMessages.ERROR_PASSWORD_FORMAT;
        }
        return null;
    }

    // パスワードと確認用パスワードが一致するかを検証する
    public String validatePasswordMatch(String password, String passwordCheck) {
        if (password == null || !password.equals(passwordCheck)) {
            return ErrorMessages.ERROR_PASSWORD_MISMATCH;
        }
        return null;
    }
}