package com.example.pf1.dto;

import com.example.pf1.constants.AccountsFormConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSettingsForm {

    // 必須項目のメールアドレスのみ
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式で入力してください。")
    @Size(max = AccountsFormConstants.EMAIL_MAX_LENGTH, message = "メールアドレスは255文字以内で設定してください。")
    private String email;

}