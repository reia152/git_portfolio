package com.example.pf1.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LoginSuccessHandler implements AuthenticationSuccessHandler {  // 

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		
		boolean isAdmin = false;
		
		for (GrantedAuthority authority : authentication.getAuthorities()) {
		    if (authority.getAuthority().equals("ROLE_ADMIN")) {
		        isAdmin = true;
		        }
		    }
		if (isAdmin) {
            // 管理者アカウントを /admin へリダイレクト
        	response.sendRedirect("/admin");
        } else {
            // 一般アカウントを / へリダイレクト
        	response.sendRedirect("/");
		}
		
	}
}