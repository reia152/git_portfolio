package com.example.pf1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pf1.dto.SettingsForm;
import com.example.pf1.entity.Accounts;
import com.example.pf1.messages.ErrorMessages;
import com.example.pf1.messages.InfoMessages;
import com.example.pf1.repository.AccountsRepository;
import com.example.pf1.service.AccountsService;
import com.example.pf1.validator.AccountsValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountsController {

    private final AccountsRepository accountsRepository;
    private final AccountsService accountsService;
    private final AccountsValidator accountsValidator;

    // GETリクエスト時（設定フォームの初期表示）
    @GetMapping("/settings")
    public String settingsForm(@AuthenticationPrincipal Accounts user, Model model) {
        SettingsForm form = new SettingsForm();
        form.setUsername(user.getUsername());  // 現在の値を初期表示
        form.setEmail(user.getEmail());
        form.setPassword("");
        model.addAttribute("settingsForm", form);
        return "pf1/user/accounts_setting";
    }

    // POSTリクエスト時（設定の保存）
    @PostMapping("/settings")
    public String settingsUpdate(
            @Valid @ModelAttribute("settingsForm") SettingsForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal Accounts user,
            RedirectAttributes redirectAttributes,
            Model model) {

        // パスワードの長さチェック（任意入力なので空なら検証しない）
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            String passwordError = accountsValidator.validatePassword(form.getPassword());
            if (passwordError != null) {
                bindingResult.rejectValue("password", "error", passwordError);
            }
        }

        // ユーザー名の重複チェック（自分自身を除外。username は @NotBlank のため null/空文字は考慮不要）
        if (accountsRepository.existsByUsernameAndUserIdNot(form.getUsername(), user.getUserId())) {
            bindingResult.rejectValue("username", "error", ErrorMessages.ERROR_USERNAME_EXISTS);
        }

        // メールアドレスの重複チェック（自分自身を除外。email も @NotBlank のため同様）
        if (accountsRepository.existsByEmailAndUserIdNot(form.getEmail(), user.getUserId())) {
            bindingResult.rejectValue("email", "error", ErrorMessages.ERROR_EMAIL_EXISTS);
        }

        if (bindingResult.hasErrors()) {
            form.setPassword("");  // 画面再表示時に入力済みパスワードを平文のまま残さない
            model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_setting";  // エラー時はフォームを再表示
        }

        try {
            // @AuthenticationPrincipalのuserはログイン時点のスナップショット（セッションに保持されたdetachedな
            // JPAエンティティ）で、以降の更新は反映されない。これをそのままsave()するとmergeとして働き、
            // 他の全カラムがログイン時点の値で上書きされてしまう（例: 別セッションでの変更が巻き戻る）。
            // そのため必ずDBから最新のエンティティを取り直し、そちらだけを書き換えて保存する。
            Accounts current = accountsRepository.findById(user.getUserId()).orElseThrow();

            // ユーザー名・メールアドレスは必須項目のため常に反映する
            current.setUsername(form.getUsername());
            current.setEmail(form.getEmail());
            if (form.getPassword() != null && !form.getPassword().isEmpty()) {
                // パスワードはハッシュ化して更新。current には直前にセットしたusername/emailも
                // 反映済みのため、updatePassword内のaccountsRepository.save(current)でまとめて保存される
                accountsService.updatePassword(current, form.getPassword());
            } else {
                accountsRepository.save(current);
            }
            // 成功メッセージをリダイレクト先に渡す（PRGパターン）
            redirectAttributes.addFlashAttribute("flashMessage", InfoMessages.INFO_EDIT_SUCCESS);
            redirectAttributes.addFlashAttribute("flashType", "success");
            return "redirect:/settings";

        } catch (Exception e) {
            log.error("設定の保存に失敗しました", e);
            form.setPassword("");
            model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_setting";
        }
    }
}