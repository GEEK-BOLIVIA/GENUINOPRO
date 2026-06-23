package com.genuino.crm.quoting.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;

import java.util.List;
import java.util.UUID;

public interface ProformaAttachmentRepository
        extends JpaRepository<ProformaAttachment, UUID> {

    List<ProformaAttachment> findByTypedProformaIdOrderByCreatedAtAsc(
            UUID typedProformaId
    );
}