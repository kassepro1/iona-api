package com.iona.ionaapi.infrastructure.dto.projet.projet;

import com.iona.ionaapi.domain.project.Project;
import org.springframework.data.domain.Page;

/**
 * Paginated response for projects
 */
public class PagedProjectResponse {
    private boolean success;
    private String message;
    private Page<Project> projects;
    private String tenantId;
    
    // Constructors
    public PagedProjectResponse() {}
    
    public PagedProjectResponse(boolean success, String message, Page<Project> projects, String tenantId) {
        this.success = success;
        this.message = message;
        this.projects = projects;
        this.tenantId = tenantId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Page<Project> getProjects() { return projects; }
    public void setProjects(Page<Project> projects) { this.projects = projects; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    // Utility methods for pagination
    public int getCurrentPage() {
        return projects != null ? projects.getNumber() : 0;
    }
    
    public int getTotalPages() {
        return projects != null ? projects.getTotalPages() : 0;
    }
    
    public long getTotalElements() {
        return projects != null ? projects.getTotalElements() : 0;
    }
    
    public int getSize() {
        return projects != null ? projects.getSize() : 0;
    }
    
    public boolean hasNext() {
        return projects != null && projects.hasNext();
    }
    
    public boolean hasPrevious() {
        return projects != null && projects.hasPrevious();
    }
}