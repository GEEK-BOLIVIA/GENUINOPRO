package com.genuino.crm.security;

import org.springframework.stereotype.Service;

@Service
public class DataScopeService {

    private final SecurityUserService securityUserService;

    public DataScopeService(SecurityUserService securityUserService) {
        this.securityUserService = securityUserService;
    }

    /**
     * Acceso global del sistema.
     * Reservado para roles administrativos/gerenciales.
     */
    public boolean canSeeEverything() {
        String role = securityUserService.getHighestRole();

        return switch (role) {
            case "OWNER",
                 "ADMIN",
                 "GERENCIA" -> true;
            default -> false;
        };
    }

    /**
     * Acceso al equipo comercial.
     *
     * JEFE_COMERCIAL puede supervisar información comercial
     * del equipo sin convertirse en administrador del sistema.
     */
    public boolean canSeeTeam() {
        String role = securityUserService.getHighestRole();

        return switch (role) {
            case "OWNER",
                 "ADMIN",
                 "GERENCIA",
                 "JEFE_COMERCIAL" -> true;
            default -> false;
        };
    }

    /**
     * El vendedor únicamente puede consultar información propia.
     */
    public boolean onlyMine() {
        return !canSeeTeam();
    }

    public String currentSeller() {
        return securityUserService.getCurrentUser();
    }

    public String currentRole() {
        return securityUserService.getHighestRole();
    }
}