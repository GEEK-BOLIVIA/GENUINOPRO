package com.genuino.crm.customerprofile;

import com.genuino.crm.customerprofile.domain.BoliviaCity;
import com.genuino.crm.customerprofile.domain.CustomerType;
import com.genuino.crm.customerprofile.domain.LeadCustomerProfile;
import com.genuino.crm.customerprofile.domain.ProformaCustomerSnapshot;
import com.genuino.crm.customerprofile.infra.BoliviaCityRepository;
import com.genuino.crm.customerprofile.infra.LeadCustomerProfileRepository;
import com.genuino.crm.customerprofile.infra.ProformaCustomerSnapshotRepository;
import com.genuino.crm.opportunity.domain.Opportunity;
import com.genuino.crm.opportunity.infra.OpportunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.Optional;

@Service
public class ProformaCustomerSnapshotService {

    private final OpportunityRepository opportunityRepository;
    private final LeadCustomerProfileRepository profileRepository;
    private final BoliviaCityRepository cityRepository;
    private final ProformaCustomerSnapshotRepository snapshotRepository;

    public ProformaCustomerSnapshotService(
            OpportunityRepository opportunityRepository,
            LeadCustomerProfileRepository profileRepository,
            BoliviaCityRepository cityRepository,
            ProformaCustomerSnapshotRepository snapshotRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.profileRepository = profileRepository;
        this.cityRepository = cityRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional
    public ProformaCustomerSnapshot capture(
            UUID proformaId,
            String opportunityId
    ) {
        if (proformaId == null) {
            throw new IllegalArgumentException(
                    "El ID de la proforma es obligatorio."
            );
        }

        if (opportunityId == null || opportunityId.isBlank()) {
            throw new IllegalArgumentException(
                    "La proforma debe estar asociada a una oportunidad."
            );
        }

        Opportunity opportunity = opportunityRepository
                .findById(opportunityId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe la oportunidad asociada."
                        )
                );

        String leadId = opportunity.leadInboxId;

        if (leadId == null || leadId.isBlank()) {
            throw new IllegalStateException(
                    "La oportunidad no tiene un lead asociado."
            );
        }

        LeadCustomerProfile profile = profileRepository
                .findById(leadId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Debe completar los datos del cliente antes de generar la proforma."
                        )
                );

        validateProfile(profile);

        ProformaCustomerSnapshot snapshot = snapshotRepository
                .findById(proformaId)
                .orElseGet(ProformaCustomerSnapshot::new);

        snapshot.setProformaId(proformaId);
        snapshot.setCustomerType(profile.getCustomerType());
        snapshot.setSourceLeadId(leadId);

        snapshot.setFullName(profile.getFullName());
        snapshot.setCityCode(profile.getCityCode());
        snapshot.setMobilePhone(profile.getMobilePhone());

        snapshot.setLegalName(profile.getLegalName());
        snapshot.setTaxId(profile.getTaxId());
        snapshot.setCompanyPhone(profile.getCompanyPhone());
        snapshot.setAddressText(profile.getAddressText());
        snapshot.setMapsUrl(profile.getMapsUrl());
        snapshot.setLatitude(profile.getLatitude());
        snapshot.setLongitude(profile.getLongitude());
        snapshot.setLegalRepresentativeName(
                profile.getLegalRepresentativeName()
        );

        if (profile.getCityCode() != null) {
            BoliviaCity city = cityRepository
                    .findById(profile.getCityCode())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "La ciudad del cliente ya no existe en el catálogo."
                            )
                    );

            snapshot.setCityName(city.getName());
            snapshot.setDepartmentName(city.getDepartment());
        } else {
            snapshot.setCityName(null);
            snapshot.setDepartmentName(null);
        }

        return snapshotRepository.save(snapshot);
    }

    @Transactional(readOnly = true)
    public ProformaCustomerSnapshot getByProformaId(UUID proformaId) {
        return snapshotRepository.findById(proformaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La proforma no tiene datos históricos del cliente."
                        )
                );
    }

    private void validateProfile(LeadCustomerProfile profile) {
        CustomerType type = profile.getCustomerType();

        if (type == null || CustomerType.UNDEFINED.equals(type)) {
            throw new IllegalStateException(
                    "Debe definir si el cliente es Persona natural o Empresa antes de generar la proforma."
            );
        }

        if (CustomerType.NATURAL_PERSON.equals(type)) {
            require(
                    profile.getFullName(),
                    "Falta el nombre completo del cliente."
            );

            require(
                    profile.getCityCode(),
                    "Falta la ciudad del cliente."
            );

            require(
                    profile.getMobilePhone(),
                    "Falta el celular del cliente."
            );
        }

        if (CustomerType.COMPANY.equals(type)) {
            require(
                    profile.getLegalName(),
                    "Falta la razón social."
            );

            require(
                    profile.getTaxId(),
                    "Falta el NIT."
            );

            require(
                    profile.getCompanyPhone(),
                    "Falta el teléfono de la empresa."
            );

            require(
                    profile.getCityCode(),
                    "Falta la ciudad de la empresa."
            );

            require(
                    profile.getAddressText(),
                    "Falta la dirección de la empresa."
            );

            require(
                    profile.getLegalRepresentativeName(),
                    "Falta el representante legal."
            );
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProformaCustomerSnapshot> findByProformaId(
            UUID proformaId
    ) {
        if (proformaId == null) {
            return Optional.empty();
        }

        return snapshotRepository.findById(proformaId);
    }

    private void require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(message);
        }
    }
}