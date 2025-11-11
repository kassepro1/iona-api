package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.contractor.Contractor;
import org.springframework.data.domain.Page;

/**
 * Paginated response for contractors
 */
public class PagedContractorResponse {
    private boolean success;
    private String message;
    private Page<Contractor> contractors;
    private String tenantId;
    
    // Constructors
    public PagedContractorResponse() {}
    
    public PagedContractorResponse(boolean success, String message, Page<Contractor> contractors, String tenantId) {
        this.success = success;
        this.message = message;
        this.contractors = contractors;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Page<Contractor> getContractors() { return contractors; }
    public void setContractors(Page<Contractor> contractors) { this.contractors = contractors; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    // Utility methods for pagination
    public int getCurrentPage() {
        return contractors != null ? contractors.getNumber() : 0;
    }
    
    public int getTotalPages() {
        return contractors != null ? contractors.getTotalPages() : 0;
    }
    
    public long getTotalElements() {
        return contractors != null ? contractors.getTotalElements() : 0;
    }
    
    public int getSize() {
        return contractors != null ? contractors.getSize() : 0;
    }
    
    public boolean hasNext() {
        return contractors != null && contractors.hasNext();
    }
    
    public boolean hasPrevious() {
        return contractors != null && contractors.hasPrevious();
    }
}