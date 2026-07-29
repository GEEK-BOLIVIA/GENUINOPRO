package com.genuino.crm.customerprofile.web;

import com.genuino.crm.customerprofile.LeadCustomerProfileService;
import com.genuino.crm.customerprofile.domain.BoliviaCity;
import com.genuino.crm.customerprofile.dto.LeadCustomerProfileResponse;
import com.genuino.crm.customerprofile.dto.UpsertLeadCustomerProfileRequest;
import com.genuino.crm.customerprofile.infra.BoliviaCityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LeadCustomerProfileController {

    private final LeadCustomerProfileService profileService;
    private final BoliviaCityRepository cityRepository;

    public LeadCustomerProfileController(
            LeadCustomerProfileService profileService,
            BoliviaCityRepository cityRepository
    ) {
        this.profileService = profileService;
        this.cityRepository = cityRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/catalogs/bolivia-cities")
    public ResponseEntity<List<BoliviaCity>> findCities() {
        return ResponseEntity.ok(
                cityRepository.findByActiveTrueOrderBySortOrderAscNameAsc()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/leads/{leadId}/customer-profile")
    public ResponseEntity<LeadCustomerProfileResponse> getProfile(
            @PathVariable String leadId
    ) {
        return ResponseEntity.ok(
                profileService.getByLeadId(leadId)
        );
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','SUPERVISOR','JEFE_COMERCIAL','GERENCIA','ADMIN','OWNER')")
    @PutMapping("/leads/{leadId}/customer-profile")
    public ResponseEntity<LeadCustomerProfileResponse> saveProfile(
            @PathVariable String leadId,
            @RequestBody UpsertLeadCustomerProfileRequest request
    ) {
        return ResponseEntity.ok(
                profileService.upsert(leadId, request)
        );
    }
}