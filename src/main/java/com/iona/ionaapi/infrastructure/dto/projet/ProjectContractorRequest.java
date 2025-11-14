package com.iona.ionaapi.infrastructure.dto.projet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Project contractor request (for associating contractors to projects)
 */
public class ProjectContractorRequest {
    private UUID contractorId;
    private String role;
    private String name;
    private BigDecimal contractAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Constructors
    public ProjectContractorRequest() {}
    
    // Getters and Setters
    public UUID getContractorId() { return contractorId; }
    public void setContractorId(UUID contractorId) { this.contractorId = contractorId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}