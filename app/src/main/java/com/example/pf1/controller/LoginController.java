package com.example.pf1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pf1.messages.ErrorMessages;

@Controller
public class LoginController {

    // GETリクエスト時（ログインフォームの初期表示）
    @GetMapping("/login")
    public String loginForm(
            @RequestParam(value = "error", required = false) String error,  // ログイン失敗時は "error" パラメータが付く
            Model model) {
        if (error != null) {
            // Spring Security がログイン失敗時に /login?error にリダイレクトする
            model.addAttribute("flashMessage", ErrorMessages.ERROR_LOGIN_FAILED);
            model.addAttribute("flashType", "error");
        }
        return "pf1/public/login";
    }
}