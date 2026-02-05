package com.example.EyeCareHubDB.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.EyeCareHubDB.entity.Account;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Account account) {
            return account;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        Account account = getCurrentAccount();
        return account != null ? account.getId() : null;
    }
}
