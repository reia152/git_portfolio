package com.example.pf1.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.pf1.constants.AccountsValues;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACCOUNTS")
@Getter
@Setter
public class Accounts implements UserDetails {  // UserDetails: Spring Security のログイン認証情報として扱うためのインターフェース

    // Long → MySQL BIGINT（DB項目定義書のとおり。auto_incrementの主キーにはBIGINTが推奨）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME", unique = true, nullable = false, length = 255)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 128)
    private String password;

    @Column(name = "FURIGANA", length = 255)
    private String furigana;

    @Column(name = "EMAIL", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "GENDER", length = 1)
    private String gender;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "PROFILE", length = 1500)
    private String profile;

    @Column(name = "PROFILE_IMAGE", length = 255)
    private String profileImage;

    @Column(name = "STATUS", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = AccountsValues.STATUS_ACTIVE;

    @Column(name = "PERMISSIONS", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer permissions = AccountsValues.PERMISSIONS_GENERAL;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "IS_DELETED", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer isDeleted = AccountsValues.IS_DELETED_FALSE;

    @CreationTimestamp  // 自動で現在日時を設定
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp  // 更新時に自動で現在日時を設定
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // ユーザーの権限リストを返す（Spring Security が内部で使用）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (permissions != null && permissions == AccountsValues.PERMISSIONS_ADMIN)
                ? "ROLE_ADMIN" : "ROLE_GENERAL";
        return List.of(new SimpleGrantedAuthority(role));
    }

    // status が ACTIVE、かつ論理削除されていないときのみ true
    @Override
    public boolean isEnabled() {
        return status != null && status == AccountsValues.STATUS_ACTIVE
                && isDeleted != null && isDeleted == AccountsValues.IS_DELETED_FALSE;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}