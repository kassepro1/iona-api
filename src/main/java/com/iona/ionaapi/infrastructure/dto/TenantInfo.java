package com.iona.ionaapi.infrastructure.dto;

public class TenantInfo {
        private String tenantId;
        private String schemaName;
        private boolean exists;
        
        public TenantInfo(String tenantId, String schemaName, boolean exists) {
            this.tenantId = tenantId;
            this.schemaName = schemaName;
            this.exists = exists;
        }
        
        // Getters et Setters
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }
        
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
    }