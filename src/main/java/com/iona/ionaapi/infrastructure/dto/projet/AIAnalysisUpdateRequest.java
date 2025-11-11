package com.iona.ionaapi.infrastructure.dto.projet;

import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;

import java.math.BigDecimal;

/**
 * AI Analysis update request
 */
public class AIAnalysisUpdateRequest {
    private AIAnalysisStatus status;
    private BigDecimal complianceScore;
    private String summary;
    private Integer issuesCount;
    
    // Constructors
    public AIAnalysisUpdateRequest() {}
    
    // Getters and Setters
    public AIAnalysisStatus getStatus() { return status; }
    public void setStatus(AIAnalysisStatus status) { this.status = status; }
    
    public BigDecimal getComplianceScore() { return complianceScore; }
    public void setComplianceScore(BigDecimal complianceScore) { this.complianceScore = complianceScore; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public Integer getIssuesCount() { return issuesCount; }
    public void setIssuesCount(Integer issuesCount) { this.issuesCount = issuesCount; }
}
