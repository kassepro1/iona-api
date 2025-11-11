package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.contractor.Contractor;

/**
 * Contractor operation response
 */
public class ContractorResponse {
    private boolean success;
    private String message;
    private Contractor contractor;
    private String tenantId;
    
    // Constructors
    public ContractorResponse() {}
    
    public ContractorResponse(boolean success, String message, Contractor contractor, String tenantId) {
        this.success = success;
        this.message = message;
        this.contractor = contractor;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}