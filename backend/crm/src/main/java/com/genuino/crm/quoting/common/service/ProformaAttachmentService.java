package com.genuino.crm.quoting.common.service;

import com.genuino.crm.quoting.common.domain.ProformaAttachment;
import com.genuino.crm.quoting.common.ProformaAttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    public ProformaAttachment uploadImage(
            UUID typedProformaId,
            MultipartFile file,
            String attachmentType,
            String title,
            String description
    ) {

        try {

            String originalName = file.getOriginalFilename();

            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")
                );
            }

            String storedName =
                    UUID.randomUUID() + extension;

            Path uploadDir = Paths.get(
                    "uploads",
                    "proformas",
                    typedProformaId.toString()
            );

            Files.createDirectories(uploadDir);

            Path targetFile =
                    uploadDir.resolve(storedName);

            Files.copy(
                    file.getInputStream(),
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

            ProformaAttachment attachment =
                    new ProformaAttachment();

            attachment.setTypedProformaId(
                    typedProformaId
            );

            attachment.setAttachmentType(
                    attachmentType
            );

            attachment.setTitle(title);

            attachment.setDescription(description);

            attachment.setFileName(storedName);

            attachment.setAttachmentUrl(
                    "/uploads/proformas/"
                            + typedProformaId
                            + "/"
                            + storedName
            );

            return repository.save(attachment);

        } catch (IOException ex) {
            throw new RuntimeException(
                    "No se pudo almacenar la imagen",
                    ex
            );
        }
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}