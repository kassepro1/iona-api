package com.iona.ionaapi.domain.project;

import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Project entity - central entity linking master contractors and contractors
 * Represents a construction project with insurance validation
 */
@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "cost", precision = 15, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "opening_date")
    private LocalDate openingDate;
    
    @Column(name = "mission", columnDefinition = "TEXT")
    private String mission;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status = ProjectStatus.PLANNED;


    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    
    // Reference to master contractor (owner of the project)
    @Column(name = "owner_id")
    private UUID ownerId;  // Will be linked to MasterContractor
    
    @Embedded
    private ProjectAIAnalysis aiAnalysis;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectContractor> projectContractors = new ArrayList<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectDocument> documents = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
    
    // Constructors
    public Project() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
        this.aiAnalysis = new ProjectAIAnalysis();
    }
    
    public Project(String name, String address, UUID ownerId) {
        this();
        this.name = name;
        this.address = address;
        this.ownerId = ownerId;
    }
    
    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        lastUpdatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = Instant.now();
    }
    
    // Utility methods to manage project contractors
    public void addContractor(UUID contractorId, String role, BigDecimal contractAmount) {
        ProjectContractor projectContractor = new ProjectContractor();
        projectContractor.setProject(this);
        projectContractor.setContractorId(contractorId);
        projectContractor.setRole(role);
        projectContractor.setContractAmount(contractAmount);
        projectContractors.add(projectContractor);
    }
    
    public void removeContractor(UUID contractorId) {
        projectContractors.removeIf(pc -> pc.getContractorId().equals(contractorId));
    }
    
    public boolean hasContractor(UUID contractorId) {
        return projectContractors.stream()
                .anyMatch(pc -> pc.getContractorId().equals(contractorId));
    }
    
    // Utility methods for documents
    public void addDocument(String documentName, String documentType, String filePath) {
        ProjectDocument document = new ProjectDocument();
        document.setProject(this);
        document.setDocumentName(documentName);
        document.setDocumentType(documentType);
        document.setFilePath(filePath);
        documents.add(document);
    }
    
    // Business logic methods
    public boolean isActive() {
        return status == ProjectStatus.STARTED || status == ProjectStatus.IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }
    
    public boolean needsInsuranceValidation() {
        return aiAnalysis.getStatus() == AIAnalysisStatus.PENDING ||
               aiAnalysis.getStatus() == AIAnalysisStatus.NON_COMPLIANT;
    }
    
    public BigDecimal getTotalContractorCosts() {
        return projectContractors.stream()
                .map(ProjectContractor::getContractAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    
    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
    
    public String getMission() { return mission; }
    public void setMission(String mission) { this.mission = mission; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    
    public ProjectAIAnalysis getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(ProjectAIAnalysis aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    
    public List<ProjectContractor> getProjectContractors() { return projectContractors; }
    public void setProjectContractors(List<ProjectContractor> projectContractors) { 
        this.projectContractors = projectContractors; 
    }
    
    public List<ProjectDocument> getDocuments() { return documents; }
    public void setDocuments(List<ProjectDocument> documents) { this.documents = documents; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return String.format("Project{id=%s, name='%s', status=%s, ownerId=%s, contractorsCount=%d}", 
                           id, name, status, ownerId, projectContractors.size());
    }
}