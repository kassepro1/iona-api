package com.iona.ionaapi.domain.project;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Association entity between Project and Contractor
 */
@Entity
@Table(name = "project_contractors")
public class ProjectContractor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Column(name = "contractor_id")
    private UUID contractorId;  // Reference to Contractor
    
    @Column(name = "role")
    private String role;  // Role in the project (e.g., "Électricité", "Plomberie")
    
    @Column(name = "contract_amount", precision = 15, scale = 2)
    private BigDecimal contractAmount;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructors
    public ProjectContractor() {
        this.createdAt = Instant.now();
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
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
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}