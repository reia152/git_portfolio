package com.example.pf1.dto;

import com.example.pf1.constants.AccountsFormConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = AccountsFormConstants.USERNAME_MAX_LENGTH, message = "ユーザー名は255文字以内で設定してください。")
    private String username;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式で入力してください。")
    @Size(max = AccountsFormConstants.EMAIL_MAX_LENGTH, message = "メールアドレスは255文字以内で設定してください。")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    private String password;

    @NotBlank(message = "パスワード確認は必須です")
    private String passwordCheck;

    @Size(max = AccountsFormConstants.FURIGANA_MAX_LENGTH, message = "ふりがなは255文字以内で設定してください。")
    private String furigana;

    private String gender;

    @Min(value = AccountsFormConstants.AGE_MIN, message = "年齢は0〜999で設定してください。")
    @Max(value = AccountsFormConstants.AGE_MAX, message = "年齢は0〜999で設定してください。")
    private Integer age;

    @Size(max = AccountsFormConstants.PROFILE_MAX_LENGTH, message = "自己紹介は1500文字以内で設定してください。")
    private String profile;

    // プロフィール画像は MultipartFile ではなく、保存後のパスを格納する
    private String profileImagePath;
}