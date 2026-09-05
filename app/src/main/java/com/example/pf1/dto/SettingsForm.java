package com.example.pf1.dto;

import com.example.pf1.constants.AccountsFormConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsForm {

    // ユーザー名・メールアドレスは必須項目（パスワードのみ任意入力）
    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = AccountsFormConstants.USERNAME_MAX_LENGTH, message = "ユーザー名は255文字以内で設定してください。")
    private String username;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式で入力してください。")
    @Size(max = AccountsFormConstants.EMAIL_MAX_LENGTH, message = "メールアドレスは255文字以内で設定してください。")
    private String email;

    // パスワードは任意入力（空欄なら変更しない）ため @NotBlank は付けない
    @Size(max = AccountsFormConstants.PASSWORD_MAX_LENGTH, message = "パスワードは32文字以内で設定してください。")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "パスワードは半角英数字と_-のみ使用可能です。")
    private String password;
}
