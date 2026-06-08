package com.genuino.crm.client.dto;

import java.util.UUID;

public class CreateClientAccountRequest {

    private String leadId;
    private UUID acceptedProformaId;

    private String companyName;
    private String contactName;
    private String email;
    private String phone;
    private String username;

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
}