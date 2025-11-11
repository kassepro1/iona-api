package com.iona.ionaapi.infrastructure.dto.projet.projet;

import java.math.BigDecimal;
import java.util.List;

/**
 * Project dashboard statistics
 */
public class ProjectDashboard {
    private long totalProjects;
    private long activeProjects;
    private long completedProjects;
    private long pendingAnalysis;
    private long compliantProjects;
    private long nonCompliantProjects;
    private BigDecimal totalValue;
    private BigDecimal averageComplianceScore;
    private List<ProjectSummary> recentProjects;
    private List<ProjectSummary> urgentProjects;
    
    // Constructors
    public ProjectDashboard() {}
    
    // Getters and Setters
    public long getTotalProjects() { return totalProjects; }
    public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }
    
    public long getActiveProjects() { return activeProjects; }
    public void setActiveProjects(long activeProjects) { this.activeProjects = activeProjects; }
    
    public long getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(long completedProjects) { this.completedProjects = completedProjects; }
    
    public long getPendingAnalysis() { return pendingAnalysis; }
    public void setPendingAnalysis(long pendingAnalysis) { this.pendingAnalysis = pendingAnalysis; }
    
    public long getCompliantProjects() { return compliantProjects; }
    public void setCompliantProjects(long compliantProjects) { this.compliantProjects = compliantProjects; }
    
    public long getNonCompliantProjects() { return nonCompliantProjects; }
    public void setNonCompliantProjects(long nonCompliantProjects) { this.nonCompliantProjects = nonCompliantProjects; }
    
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    
    public BigDecimal getAverageComplianceScore() { return averageComplianceScore; }
    public void setAverageComplianceScore(BigDecimal averageComplianceScore) { this.averageComplianceScore = averageComplianceScore; }
    
    public List<ProjectSummary> getRecentProjects() { return recentProjects; }
    public void setRecentProjects(List<ProjectSummary> recentProjects) { this.recentProjects = recentProjects; }
    
    public List<ProjectSummary> getUrgentProjects() { return urgentProjects; }
    public void setUrgentProjects(List<ProjectSummary> urgentProjects) { this.urgentProjects = urgentProjects; }
}