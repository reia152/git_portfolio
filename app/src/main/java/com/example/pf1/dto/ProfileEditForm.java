package com.example.pf1.dto;

import com.example.pf1.constants.AccountsFormConstants;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileEditForm {

    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = AccountsFormConstants.USERNAME_MAX_LENGTH, message = "ユーザー名は255文字以内で設定してください。")
    private String username;

    @Size(max = AccountsFormConstants.FURIGANA_MAX_LENGTH, message = "ふりがなは255文字以内で設定してください。")
    private String furigana;

    private String gender;

    @Min(value = AccountsFormConstants.AGE_MIN, message = "年齢は0〜999で設定してください。")
    @Max(value = AccountsFormConstants.AGE_MAX, message = "年齢は0〜999で設定してください。")
    private Integer age;

    @Size(max = AccountsFormConstants.PROFILE_MAX_LENGTH, message = "自己紹介は1500文字以内で設定してください。")
    private String profile;

    private String profileImagePath;
}