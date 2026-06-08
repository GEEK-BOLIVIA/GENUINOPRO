package com.genuino.crm.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService {

    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            return "system";
        }

        return auth.getName();
    }

    public String getHighestRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return "VENDEDOR";
        }

        if (hasRole(auth, "OWNER")) return "OWNER";
        if (hasRole(auth, "ADMIN")) return "ADMIN";
        if (hasRole(auth, "GERENCIA")) return "GERENCIA";
        if (hasRole(auth, "JEFE_COMERCIAL")) return "JEFE_COMERCIAL";
        if (hasRole(auth, "SUPERVISOR")) return "SUPERVISOR";
        if (hasRole(auth, "VENDEDOR")) return "VENDEDOR";

        return "VENDEDOR";
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}