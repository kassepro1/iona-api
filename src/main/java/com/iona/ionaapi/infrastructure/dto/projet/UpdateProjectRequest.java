package com.iona.ionaapi.infrastructure.dto.projet;

import com.iona.ionaapi.domain.project.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Project update request
 */
public class UpdateProjectRequest {
    private String name;
    private String address;
    private BigDecimal cost;
    private LocalDate openingDate;
    private String mission;
    private String ownerName;
    private UUID ownerId;
    private ProjectStatus status;
    private List<ProjectContractorRequest> contractors;
    
    // Constructors
    public UpdateProjectRequest() {}
    
    // Getters and Setters
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
    
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public List<ProjectContractorRequest> getContractors() { return contractors; }
    public void setContractors(List<ProjectContractorRequest> contractors) { this.contractors = contractors; }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
