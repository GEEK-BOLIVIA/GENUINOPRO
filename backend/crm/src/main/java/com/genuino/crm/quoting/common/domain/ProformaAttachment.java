package com.genuino.crm.quoting.common.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;



@Entity
@Table(name = "proforma_attachment")
public class ProformaAttachment {

    @Id
    private UUID id;

    @JsonProperty("typedProformaId")
    @Column(name = "typed_proforma_id", nullable = false)
    private UUID typedProformaId;

    @JsonProperty("attachmentType")

    @Column(name = "attachment_type", nullable = false, length = 30)
    private String attachmentType;

    


    @Column(name = "title")
    private String title;

    @JsonProperty("attachmentUrl")

    @Column(name = "attachment_url", nullable = false, columnDefinition = "text")
    private String attachmentUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getTypedProformaId() {
        return typedProformaId;
    }

    public void setTypedProformaId(UUID typedProformaId) {
        this.typedProformaId = typedProformaId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}