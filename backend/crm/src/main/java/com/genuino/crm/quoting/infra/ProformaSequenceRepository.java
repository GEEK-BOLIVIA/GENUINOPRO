package com.genuino.crm.quoting.infra;

import com.genuino.crm.quoting.domain.ProformaSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProformaSequenceRepository extends JpaRepository<ProformaSequence, ProformaSequence.Pk> {
}