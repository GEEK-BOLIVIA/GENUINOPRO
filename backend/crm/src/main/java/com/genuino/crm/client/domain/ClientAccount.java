package com.genuino.crm.client.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_accounts")
public class ClientAccount {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String leadId;

    @Column(nullable = false)
    private UUID acceptedProformaId;

    private String companyName;
    private String contactName;
    private String email;
    private String phone;
    private String username;

    private boolean active;

    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public UUID getAcceptedProformaId() { return acceptedProformaId; }
    public void setAcceptedProformaId(UUID acceptedProformaId) { this.acceptedProformaId = acceptedProformaId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}