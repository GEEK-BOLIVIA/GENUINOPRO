package com.genuino.crm.customerprofile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "proforma_customer_snapshot")
public class ProformaCustomerSnapshot {

    @Id
    @Column(name = "proforma_id", nullable = false)
    private UUID proformaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 30)
    private CustomerType customerType;

    // Persona natural

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "city_code", length = 50)
    private String cityCode;

    @Column(name = "city_name", length = 120)
    private String cityName;

    @Column(name = "department_name", length = 120)
    private String departmentName;

    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    // Empresa

    @Column(name = "legal_name", length = 250)
    private String legalName;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "company_phone", length = 50)
    private String companyPhone;

    @Column(name = "address_text", length = 500)
    private String addressText;

    @Column(name = "maps_url", columnDefinition = "TEXT")
    private String mapsUrl;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "legal_representative_name", length = 250)
    private String legalRepresentativeName;

    @Column(name = "source_lead_id")
    private String sourceLeadId;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @PrePersist
    public void prePersist() {
        if (capturedAt == null) {
            capturedAt = Instant.now();
        }
    }

    public UUID getProformaId() {
        return proformaId;
    }

    public void setProformaId(UUID proformaId) {
        this.proformaId = proformaId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public String getMapsUrl() {
        return mapsUrl;
    }

    public void setMapsUrl(String mapsUrl) {
        this.mapsUrl = mapsUrl;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getLegalRepresentativeName() {
        return legalRepresentativeName;
    }

    public void setLegalRepresentativeName(String legalRepresentativeName) {
        this.legalRepresentativeName = legalRepresentativeName;
    }

    public String getSourceLeadId() {
        return sourceLeadId;
    }

    public void setSourceLeadId(String sourceLeadId) {
        this.sourceLeadId = sourceLeadId;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }
}