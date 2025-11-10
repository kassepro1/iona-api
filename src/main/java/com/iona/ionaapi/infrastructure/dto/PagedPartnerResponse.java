package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.partner.Partner;
import org.springframework.data.domain.Page;

public class PagedPartnerResponse {
    private boolean success;
    private String message;
    private Page<Partner> partners;
    private String tenantId;
    
    // Constructeurs
    public PagedPartnerResponse() {}
    
    public PagedPartnerResponse(boolean success, String message, Page<Partner> partners, String tenantId) {
        this.success = success;
        this.message = message;
        this.partners = partners;
        this.tenantId = tenantId;
    }
    
    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Page<Partner> getPartners() { return partners; }
    public void setPartners(Page<Partner> partners) { this.partners = partners; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    // Méthodes utilitaires pour la pagination
    public int getCurrentPage() {
        return partners != null ? partners.getNumber() : 0;
    }
    
    public int getTotalPages() {
        return partners != null ? partners.getTotalPages() : 0;
    }
    
    public long getTotalElements() {
        return partners != null ? partners.getTotalElements() : 0;
    }
    
    public int getSize() {
        return partners != null ? partners.getSize() : 0;
    }
    
    public boolean hasNext() {
        return partners != null && partners.hasNext();
    }
    
    public boolean hasPrevious() {
        return partners != null && partners.hasPrevious();
    }
}