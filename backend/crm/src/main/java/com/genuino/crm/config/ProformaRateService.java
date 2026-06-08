package com.genuino.crm.config;

import com.genuino.crm.config.domain.ProformaRate;
import com.genuino.crm.config.infra.ProformaRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProformaRateService {

    private final ProformaRateRepository repository;

    public ProformaRateService(ProformaRateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProformaRate> findByType(String proformaType) {
        return repository.findByProformaTypeAndActiveTrueOrderByRateTypeAscRangeFromAsc(
                proformaType.toUpperCase()
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal findRatePrice(String proformaType, String rateType, BigDecimal value) {

        String safeProformaType = proformaType.toUpperCase();
        String safeRateType = rateType.toUpperCase();

        return repository
                .findFirstByProformaTypeAndRateTypeAndActiveTrueAndRangeFromLessThanEqualAndRangeToGreaterThanEqual(
                        safeProformaType,
                        safeRateType,
                        value,
                        value
                )
                .or(() -> repository
                        .findFirstByProformaTypeAndRateTypeAndActiveTrueAndRangeFromLessThanEqualAndRangeToIsNull(
                                safeProformaType,
                                safeRateType,
                                value
                        )
                )
                .map(ProformaRate::getPrice)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe tarifa activa para " + safeProformaType + " / " + safeRateType + " con valor " + value
                ));
    }

    @Transactional
    public ProformaRate update(UUID id, ProformaRate request) {
        ProformaRate current = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

        current.setRangeFrom(request.getRangeFrom());
        current.setRangeTo(request.getRangeTo());
        current.setPrice(request.getPrice());
        current.setCurrency(request.getCurrency());
        current.setActive(request.getActive());

        return repository.save(current);
    }

@Transactional
public ProformaRate create(ProformaRate request) {
    request.setId(null);

    if (request.getProformaType() != null) {
        request.setProformaType(request.getProformaType().toUpperCase());
    }

    if (request.getRateType() != null) {
        request.setRateType(request.getRateType().toUpperCase());
    }

    if (request.getActive() == null) {
        request.setActive(true);
    }

    return repository.save(request);
}

@Transactional
public void deactivate(UUID id) {
    ProformaRate current = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

    current.setActive(false);
    repository.save(current);
}
}