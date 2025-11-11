package com.iona.ionaapi.infrastructure.dto.projet.projet;

import com.iona.ionaapi.domain.project.Project;
import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;

import java.math.BigDecimal;

/**
 * Project summary for dashboard and lists (lighter version)
 */
public class ProjectSummary {
    private String id;
    private String name;
    private String address;
    private BigDecimal cost;
    private ProjectStatus status;
    private AIAnalysisStatus aiStatus;
    private Integer compliancePercent;
    private Integer contractorsCount;
    private String ownerName;  // Will be populated via service calls
    
    // Constructors
    public ProjectSummary() {}
    
    public ProjectSummary(Project project) {
        this.id = project.getId().toString();
        this.name = project.getName();
        this.address = project.getAddress();
        this.cost = project.getCost();
        this.status = project.getStatus();
        this.aiStatus = project.getAiAnalysis().getStatus();
        this.compliancePercent = project.getAiAnalysis().getProgressPercent();
        this.contractorsCount = project.getProjectContractors().size();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public AIAnalysisStatus getAiStatus() { return aiStatus; }
    public void setAiStatus(AIAnalysisStatus aiStatus) { this.aiStatus = aiStatus; }
    
    public Integer getCompliancePercent() { return compliancePercent; }
    public void setCompliancePercent(Integer compliancePercent) { this.compliancePercent = compliancePercent; }
    
    public Integer getContractorsCount() { return contractorsCount; }
    public void setContractorsCount(Integer contractorsCount) { this.contractorsCount = contractorsCount; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}