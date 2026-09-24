package com.example.pf1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pf1.dto.AdminSettingsForm;
import com.example.pf1.entity.Accounts;
import com.example.pf1.messages.ErrorMessages;
import com.example.pf1.repository.AccountsRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

	private final AccountsRepository accountsRepository;

	// GETリクエスト時（設定フォームの初期表示）
	@GetMapping("/settings/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String settingsAdminForm(@AuthenticationPrincipal Accounts user, Model model) {
		AdminSettingsForm form = new AdminSettingsForm();
		form.setEmail(user.getEmail());
		model.addAttribute("settingsAdminForm", form);
		return "pf1/admin/settings_admin";
	}

	// POSTリクエスト時（設定の保存）
	@PostMapping("/settings/admin")
	public String settingsUpdate(
			@Valid @ModelAttribute("settingsAdminForm") AdminSettingsForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal Accounts user,
			RedirectAttributes redirectAttributes,
			Model model) {

		// メールアドレスの重複チェック（自分自身を除外。email も @NotBlank のため同様）
		if (accountsRepository.existsByEmailAndUserIdNot(form.getEmail(), user.getUserId())) {
			bindingResult.rejectValue("email", "error", ErrorMessages.ERROR_EMAIL_EXISTS);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
			model.addAttribute("flashType", "error");
			return "pf1/admin/settings_admin";
		}

		try {
			Accounts current = accountsRepository.findById(user.getUserId()).orElseThrow();
			current.setEmail(form.getEmail());
			accountsRepository.save(current);
			return "redirect:/settings/admin";

		} catch (Exception e) {
			log.error("設定の保存に失敗しました", e);
			model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
			model.addAttribute("flashType", "error");
			return "pf1/admin/settings_admin";
		}
	}
}