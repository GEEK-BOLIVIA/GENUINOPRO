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
        public List<ProformaRate> findByType(String proformaType, boolean includeInactive) {
        String safeType = proformaType.toUpperCase();

        if (includeInactive) {
                return repository.findByProformaTypeOrderByActiveDescRateTypeAscRangeFromAsc(safeType);
        }

        return repository.findByProformaTypeAndActiveTrueOrderByRateTypeAscRangeFromAsc(safeType);
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

    validate(current);

    return repository.save(current);
}

@Transactional
public ProformaRate create(ProformaRate request) {
    request.setId(null);

    if (request.getActive() == null) {
        request.setActive(true);
    }

    validate(request);

    return repository.save(request);
}

@Transactional
public void deactivate(UUID id) {
    ProformaRate current = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

    current.setActive(false);
    repository.save(current);
}

@Transactional
public ProformaRate activate(UUID id) {
    ProformaRate current = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

    current.setActive(true);

    return repository.save(current);
}

private void validate(ProformaRate rate) {
    if (rate.getProformaType() == null || rate.getProformaType().isBlank()) {
        throw new IllegalArgumentException("El tipo de proforma es obligatorio");
    }

    if (rate.getRateType() == null || rate.getRateType().isBlank()) {
        throw new IllegalArgumentException("El tipo de tarifa es obligatorio");
    }

    if (rate.getPrice() == null || rate.getPrice().compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
    }

    if (rate.getRangeFrom() != null
            && rate.getRangeTo() != null
            && rate.getRangeFrom().compareTo(rate.getRangeTo()) > 0) {
        throw new IllegalArgumentException("El rango desde no puede ser mayor al rango hasta");
    }

    if (rate.getCurrency() == null || rate.getCurrency().isBlank()) {
        rate.setCurrency("USD");
    }

    if (!rate.getCurrency().equalsIgnoreCase("USD")
            && !rate.getCurrency().equalsIgnoreCase("BOB")) {
        throw new IllegalArgumentException("Moneda inválida. Use USD o BOB");
    }

    rate.setProformaType(rate.getProformaType().toUpperCase());
    rate.setRateType(rate.getRateType().toUpperCase());
    rate.setCurrency(rate.getCurrency().toUpperCase());
}
}