package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.mastercontractor.MasterContractor;

public class MasterContractorResponse {
    private boolean success;
    private String message;
    private MasterContractor masterContractor;
    private String tenantId;
    
    // Constructors
    public MasterContractorResponse() {}
    
    public MasterContractorResponse(boolean success, String message, MasterContractor masterContractor, String tenantId) {
        this.success = success;
        this.message = message;
        this.masterContractor = masterContractor;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public MasterContractor getMasterContractor() { return masterContractor; }
    public void setMasterContractor(MasterContractor masterContractor) { this.masterContractor = masterContractor; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}