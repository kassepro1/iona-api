package com.iona.ionaapi.infrastructure.dto;

public class TenantResponse {
        private boolean success;
        private String message;
        private TenantInfo tenant;
        
        public TenantResponse(boolean success, String message, TenantInfo tenant) {
            this.success = success;
            this.message = message;
            this.tenant = tenant;
        }
        
        // Getters et Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public TenantInfo getTenant() { return tenant; }
        public void setTenant(TenantInfo tenant) { this.tenant = tenant; }
    }