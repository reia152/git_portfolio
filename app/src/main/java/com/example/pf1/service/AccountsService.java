package com.example.pf1.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pf1.constants.AccountsValues;
import com.example.pf1.entity.Accounts;
import com.example.pf1.repository.AccountsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountsService {

    private final AccountsRepository accountsRepository;
    private final PasswordEncoder passwordEncoder;

    // パスワードをハッシュ化してユーザーを作成する
    public Accounts createUser(String username, String rawPassword, String email,
                                String furigana, String gender, Integer age,
                                String profile, String profileImage) {
        Accounts user = new Accounts();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFurigana(furigana);
        user.setGender(gender);
        user.setAge(age);
        user.setProfile(profile);
        user.setProfileImage(profileImage);
        user.setStatus(AccountsValues.STATUS_ACTIVE);
        user.setPermissions(AccountsValues.PERMISSIONS_GENERAL);
        user.setIsDeleted(AccountsValues.IS_DELETED_FALSE);
        return accountsRepository.save(user);
    }

    // パスワードの更新
    public void updatePassword(Accounts user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        accountsRepository.save(user);
    }
}