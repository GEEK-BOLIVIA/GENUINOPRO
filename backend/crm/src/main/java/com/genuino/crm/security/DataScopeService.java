package com.genuino.crm.security;

import org.springframework.stereotype.Service;

@Service
public class DataScopeService {

    private final SecurityUserService securityUserService;

    public DataScopeService(SecurityUserService securityUserService) {
        this.securityUserService = securityUserService;
    }

    public boolean canSeeEverything() {

        String role = securityUserService.getHighestRole();

        return switch (role) {
            case "OWNER",
                 "ADMIN",
                 "GERENCIA" -> true;
            default -> false;
        };
    }

    public String currentSeller() {
        return securityUserService.getCurrentUser();
    }

    public boolean onlyMine() {
        return !canSeeEverything();
    }
}