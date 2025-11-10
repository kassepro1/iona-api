package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.mastercontractor.MasterContractor;
import org.springframework.data.domain.Page;

/**
 * Paginated response for master contractors
 */
public class PagedMasterContractorResponse {
    private boolean success;
    private String message;
    private Page<MasterContractor> masterContractors;
    private String tenantId;
    
    // Constructors
    public PagedMasterContractorResponse() {}
    
    public PagedMasterContractorResponse(boolean success, String message, Page<MasterContractor> masterContractors, String tenantId) {
        this.success = success;
        this.message = message;
        this.masterContractors = masterContractors;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Page<MasterContractor> getMasterContractors() { return masterContractors; }
    public void setMasterContractors(Page<MasterContractor> masterContractors) { this.masterContractors = masterContractors; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    // Utility methods for pagination
    public int getCurrentPage() {
        return masterContractors != null ? masterContractors.getNumber() : 0;
    }
    
    public int getTotalPages() {
        return masterContractors != null ? masterContractors.getTotalPages() : 0;
    }
    
    public long getTotalElements() {
        return masterContractors != null ? masterContractors.getTotalElements() : 0;
    }
    
    public int getSize() {
        return masterContractors != null ? masterContractors.getSize() : 0;
    }
    
    public boolean hasNext() {
        return masterContractors != null && masterContractors.hasNext();
    }
    
    public boolean hasPrevious() {
        return masterContractors != null && masterContractors.hasPrevious();
    }
}