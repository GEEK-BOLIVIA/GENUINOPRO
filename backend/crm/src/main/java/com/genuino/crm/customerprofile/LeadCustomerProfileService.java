package com.genuino.crm.customerprofile;

import com.genuino.crm.customerprofile.domain.BoliviaCity;
import com.genuino.crm.customerprofile.domain.CustomerType;
import com.genuino.crm.customerprofile.domain.LeadCustomerProfile;
import com.genuino.crm.customerprofile.dto.LeadCustomerProfileResponse;
import com.genuino.crm.customerprofile.dto.UpsertLeadCustomerProfileRequest;
import com.genuino.crm.customerprofile.infra.BoliviaCityRepository;
import com.genuino.crm.customerprofile.infra.LeadCustomerProfileRepository;
import com.genuino.crm.lead.LeadAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadCustomerProfileService {

    private final LeadCustomerProfileRepository profileRepository;
    private final BoliviaCityRepository cityRepository;
    private final LeadAccessService leadAccessService;

    public LeadCustomerProfileService(
            LeadCustomerProfileRepository profileRepository,
            BoliviaCityRepository cityRepository,
            LeadAccessService leadAccessService
    ) {
        this.profileRepository = profileRepository;
        this.cityRepository = cityRepository;
        this.leadAccessService = leadAccessService;
    }

    @Transactional(readOnly = true)
    public LeadCustomerProfileResponse getByLeadId(String leadId) {
        leadAccessService.getAuthorizedLead(leadId);

        LeadCustomerProfile profile = profileRepository.findById(leadId)
                .orElseGet(() -> createUndefinedProfile(leadId));

        return toResponse(profile);
    }

    @Transactional
    public LeadCustomerProfileResponse upsert(
            String leadId,
            UpsertLeadCustomerProfileRequest request
    ) {
        leadAccessService.getAuthorizedLead(leadId);

        validate(request);

        LeadCustomerProfile profile = profileRepository.findById(leadId)
                .orElseGet(() -> {
                    LeadCustomerProfile created = new LeadCustomerProfile();
                    created.setLeadId(leadId);
                    return created;
                });

        profile.setCustomerType(request.getCustomerType());

        if (CustomerType.NATURAL_PERSON.equals(request.getCustomerType())) {
            applyNaturalPerson(profile, request);
            clearCompanyFields(profile);
        }

        if (CustomerType.COMPANY.equals(request.getCustomerType())) {
            applyCompany(profile, request);
            clearNaturalPersonFields(profile);
        }

        LeadCustomerProfile saved = profileRepository.save(profile);

        return toResponse(saved);
    }

    private LeadCustomerProfile createUndefinedProfile(String leadId) {
        LeadCustomerProfile profile = new LeadCustomerProfile();
        profile.setLeadId(leadId);
        profile.setCustomerType(CustomerType.UNDEFINED);

        return profile;
    }

    private void validate(UpsertLeadCustomerProfileRequest request) {
        if (request == null || request.getCustomerType() == null) {
            throw new IllegalArgumentException(
                    "El tipo de cliente es obligatorio."
            );
        }

        if (CustomerType.UNDEFINED.equals(request.getCustomerType())) {
            throw new IllegalArgumentException(
                    "Debe seleccionar Persona natural o Empresa."
            );
        }

        if (CustomerType.NATURAL_PERSON.equals(request.getCustomerType())) {
            require(request.getFullName(), "El nombre completo es obligatorio.");
            require(request.getCityCode(), "La ciudad es obligatoria.");
            require(request.getMobilePhone(), "El celular es obligatorio.");

            cityRepository.findById(request.getCityCode())
                    .filter(city -> Boolean.TRUE.equals(city.getActive()))
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "La ciudad seleccionada no es válida."
                            )
                    );
        }

        if (CustomerType.COMPANY.equals(request.getCustomerType())) {
            require(request.getLegalName(), "La razón social es obligatoria.");
            require(request.getTaxId(), "El NIT es obligatorio.");
            require(request.getCompanyPhone(), "El teléfono es obligatorio.");
            require(request.getCityCode(), "La ciudad es obligatoria.");
            require(request.getAddressText(), "La dirección es obligatoria.");
            require(
                    request.getLegalRepresentativeName(),
                    "El representante legal es obligatorio."
            );

            cityRepository.findById(request.getCityCode())
                    .filter(city -> Boolean.TRUE.equals(city.getActive()))
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "La ciudad seleccionada no es válida."
                            )
                    );
        }
    }

    private void require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void applyNaturalPerson(
            LeadCustomerProfile profile,
            UpsertLeadCustomerProfileRequest request
    ) {
        profile.setFullName(request.getFullName().trim());
        profile.setCityCode(request.getCityCode().trim());
        profile.setMobilePhone(request.getMobilePhone().trim());
    }

    private void applyCompany(
        LeadCustomerProfile profile,
        UpsertLeadCustomerProfileRequest request
    ) {
        profile.setLegalName(
                request.getLegalName().trim()
        );

        profile.setTaxId(
                request.getTaxId().trim()
        );

        profile.setCompanyPhone(
                request.getCompanyPhone().trim()
        );

        profile.setCityCode(
                request.getCityCode().trim()
        );

        profile.setAddressText(
                request.getAddressText().trim()
        );

        profile.setMapsUrl(
                trimToNull(request.getMapsUrl())
        );

        profile.setLatitude(
                request.getLatitude()
        );

        profile.setLongitude(
                request.getLongitude()
        );

        profile.setLegalRepresentativeName(
                request.getLegalRepresentativeName().trim()
        );
    }

    private void clearNaturalPersonFields(
            LeadCustomerProfile profile
    ) {
        profile.setFullName(null);
        profile.setMobilePhone(null);
    }

    private void clearCompanyFields(LeadCustomerProfile profile) {
        profile.setLegalName(null);
        profile.setTaxId(null);
        profile.setCompanyPhone(null);
        profile.setAddressText(null);
        profile.setMapsUrl(null);
        profile.setLatitude(null);
        profile.setLongitude(null);
        profile.setLegalRepresentativeName(null);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private LeadCustomerProfileResponse toResponse(
            LeadCustomerProfile profile
    ) {
        LeadCustomerProfileResponse response =
                new LeadCustomerProfileResponse();

        response.setLeadId(profile.getLeadId());
        response.setCustomerType(profile.getCustomerType());

        response.setFullName(profile.getFullName());
        response.setCityCode(profile.getCityCode());
        response.setMobilePhone(profile.getMobilePhone());

        response.setLegalName(profile.getLegalName());
        response.setTaxId(profile.getTaxId());
        response.setCompanyPhone(profile.getCompanyPhone());
        response.setAddressText(profile.getAddressText());
        response.setMapsUrl(profile.getMapsUrl());
        response.setLatitude(profile.getLatitude());
        response.setLongitude(profile.getLongitude());
        response.setLegalRepresentativeName(
                profile.getLegalRepresentativeName()
        );

        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        if (profile.getCityCode() != null) {
            cityRepository.findById(profile.getCityCode())
                    .ifPresent(city -> {
                        response.setCityName(city.getName());
                        response.setDepartmentName(city.getDepartment());
                    });
        }

        return response;
    }
}