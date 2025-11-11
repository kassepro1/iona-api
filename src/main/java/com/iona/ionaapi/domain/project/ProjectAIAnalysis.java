package com.iona.ionaapi.domain.project;

import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Embedded class for AI analysis data
 */
@Embeddable
public class ProjectAIAnalysis {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_status")
    private AIAnalysisStatus status = AIAnalysisStatus.PENDING;
    
    @Column(name = "ai_progress_percent")
    private Integer progressPercent = 0;
    
    @Column(name = "ai_last_analysis")
    private Instant lastAnalysis;
    
    @Column(name = "ai_compliance_score", precision = 5, scale = 2)
    private BigDecimal complianceScore;
    
    @Column(name = "ai_issues_count")
    private Integer issuesCount = 0;
    
    @Column(name = "ai_analysis_summary", columnDefinition = "TEXT")
    private String analysisSummary;
    
    // Constructors
    public ProjectAIAnalysis() {}
    
    // Business methods
    public boolean isCompliant() {
        return status == AIAnalysisStatus.COMPLIANT;
    }
    
    public boolean hasIssues() {
        return issuesCount != null && issuesCount > 0;
    }
    
    public void markAsAnalyzed(AIAnalysisStatus newStatus, BigDecimal score, String summary) {
        this.status = newStatus;
        this.complianceScore = score;
        this.analysisSummary = summary;
        this.lastAnalysis = Instant.now();
        
        if (newStatus == AIAnalysisStatus.COMPLIANT) {
            this.progressPercent = 100;
        }
    }
    
    // Getters and Setters
    public AIAnalysisStatus getStatus() { return status; }
    public void setStatus(AIAnalysisStatus status) { this.status = status; }
    
    public Integer getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    
    public Instant getLastAnalysis() { return lastAnalysis; }
    public void setLastAnalysis(Instant lastAnalysis) { this.lastAnalysis = lastAnalysis; }
    
    public BigDecimal getComplianceScore() { return complianceScore; }
    public void setComplianceScore(BigDecimal complianceScore) { this.complianceScore = complianceScore; }
    
    public Integer getIssuesCount() { return issuesCount; }
    public void setIssuesCount(Integer issuesCount) { this.issuesCount = issuesCount; }
    
    public String getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }
}