package com.example.pf1.controller;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pf1.entity.Accounts;
import com.example.pf1.messages.ErrorMessages;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    // GETリクエスト時（ログインフォームの初期表示）
    @GetMapping("/login")
    public String loginForm(
            @RequestParam(value = "error", required = false) String error,  // ログイン失敗時は "error" パラメータが付く
            HttpServletRequest request, Model model) {
        if (error != null) {
        	Object exception = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            // Spring Security がログイン失敗時に /login?error にリダイレクトする
        	if (exception instanceof DisabledException) {
        	    model.addAttribute("flashMessage", ErrorMessages.ERROR_ACCOUNT_DISABLED);
        	} else {
        	    model.addAttribute("flashMessage", ErrorMessages.ERROR_LOGIN_FAILED);
        	}
            model.addAttribute("flashType", "error");
        }
        return "pf1/public/login";
    }
    
 // ログイン成功後にプロフィール詳細画面にリダイレクトする
    @GetMapping("/")
    public String loginRedirect(@AuthenticationPrincipal Accounts user) {
        if (user != null) {
            // ログイン済みの場合は自分のプロフィール詳細画面にリダイレクト
            return "redirect:/profile/detail/" + user.getUserId();
        }
        return "redirect:/login";
    }
}