package com.genuino.crm.quoting.air.infra;

import com.genuino.crm.quoting.air.domain.TypedAirProforma;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TypedAirProformaRepository
        extends JpaRepository<TypedAirProforma, UUID> {
}