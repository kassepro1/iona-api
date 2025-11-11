package com.iona.ionaapi.infrastructure.dto.projet.projet;

import com.iona.ionaapi.domain.project.Project;

/**
 * Project operation response
 */
public class ProjectResponse {
    private boolean success;
    private String message;
    private Project project;
    private String tenantId;
    
    // Constructors
    public ProjectResponse() {}
    
    public ProjectResponse(boolean success, String message, Project project, String tenantId) {
        this.success = success;
        this.message = message;
        this.project = project;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}