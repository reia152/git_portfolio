package com.example.pf1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.pf1.constants.AccountsValues;
import com.example.pf1.entity.Accounts;
import com.example.pf1.repository.AccountsRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountsRepository accountsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // username・email 両方で確認することで、再起動時の unique 制約エラーを防ぐ
        if (!accountsRepository.existsByUsername("admin")
                && !accountsRepository.existsByEmail("admin@example.com")) {
            Accounts admin = new Accounts();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setEmail("admin@example.com");
            admin.setStatus(AccountsValues.STATUS_ACTIVE);
            admin.setPermissions(AccountsValues.PERMISSIONS_ADMIN);
            admin.setIsDeleted(AccountsValues.IS_DELETED_FALSE);
            accountsRepository.save(admin);
            System.out.println("管理者アカウントを作成しました: admin / admin1234");
        }
    }
}