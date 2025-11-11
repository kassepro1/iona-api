package com.iona.ionaapi.infrastructure.dto.projet.projet;

import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Project search filters
 */
public class ProjectSearchRequest {
    private String name;
    private ProjectStatus status;
    private AIAnalysisStatus aiStatus;
    private UUID ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    
    // Constructors
    public ProjectSearchRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public AIAnalysisStatus getAiStatus() { return aiStatus; }
    public void setAiStatus(AIAnalysisStatus aiStatus) { this.aiStatus = aiStatus; }
    
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public BigDecimal getMinCost() { return minCost; }
    public void setMinCost(BigDecimal minCost) { this.minCost = minCost; }
    
    public BigDecimal getMaxCost() { return maxCost; }
    public void setMaxCost(BigDecimal maxCost) { this.maxCost = maxCost; }
}
