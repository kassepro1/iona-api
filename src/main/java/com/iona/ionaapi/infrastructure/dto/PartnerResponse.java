package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.partner.Partner;

public class PartnerResponse {
    private boolean success;
    private String message;
    private Partner partner;
    private String tenantId;
    
    // Constructeurs
    public PartnerResponse() {}
    
    public PartnerResponse(boolean success, String message, Partner partner, String tenantId) {
        this.success = success;
        this.message = message;
        this.partner = partner;
        this.tenantId = tenantId;
    }
    
    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Partner getPartner() { return partner; }
    public void setPartner(Partner partner) { this.partner = partner; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}