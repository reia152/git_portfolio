package com.example.pf1.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.pf1.constants.AccountsValues;
import com.example.pf1.messages.ErrorMessages;

class AccountsValidatorTest {

    private final AccountsValidator validator = new AccountsValidator();

    @Test
    void validateStatus_有効な値の場合はnullを返す() {
        assertNull(validator.validateStatus(AccountsValues.STATUS_ACTIVE));
        assertNull(validator.validateStatus(AccountsValues.STATUS_INACTIVE));
    }

    @Test
    void validateStatus_nullの場合はnullを返す() {
        assertNull(validator.validateStatus(null));
    }

    @Test
    void validateStatus_未定義の値の場合はエラーメッセージを返す() {
        assertEquals(ErrorMessages.ERROR_STATUS_CODE, validator.validateStatus(2));
        assertEquals(ErrorMessages.ERROR_STATUS_CODE, validator.validateStatus(-1));
    }

    @Test
    void validatePermissions_有効な値の場合はnullを返す() {
        assertNull(validator.validatePermissions(AccountsValues.PERMISSIONS_ADMIN));
        assertNull(validator.validatePermissions(AccountsValues.PERMISSIONS_GENERAL));
    }

    @Test
    void validatePermissions_nullの場合はnullを返す() {
        assertNull(validator.validatePermissions(null));
    }

    @Test
    void validatePermissions_未定義の値の場合はエラーメッセージを返す() {
        assertEquals(ErrorMessages.ERROR_PERMISSIONS_CODE, validator.validatePermissions(2));
        assertEquals(ErrorMessages.ERROR_PERMISSIONS_CODE, validator.validatePermissions(-1));
    }
}