package com.genuino.crm.quoting.common.service;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.ProformaAttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProformaAttachmentService {

    private final ProformaAttachmentRepository repository;

    public ProformaAttachmentService(ProformaAttachmentRepository repository) {
        this.repository = repository;
    }

    public List<ProformaAttachment> findByProforma(UUID typedProformaId) {
        return repository.findByTypedProformaIdOrderByCreatedAtAsc(typedProformaId);
    }

    public ProformaAttachment create(ProformaAttachment attachment) {
        return repository.save(attachment);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}