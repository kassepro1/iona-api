package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;

import java.util.List;

/**
 * Specialty search request
 */
public class SpecialtySearchRequest {
    private List<ContractorSpecialty> specialties;
    private ContractorType type; // Optional filter by type
    
    // Constructors
    public SpecialtySearchRequest() {}
    
    public SpecialtySearchRequest(List<ContractorSpecialty> specialties) {
        this.specialties = specialties;
    }
    
    // Getters and Setters
    public List<ContractorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(List<ContractorSpecialty> specialties) { this.specialties = specialties; }
    
    public ContractorType getType() { return type; }
    public void setType(ContractorType type) { this.type = type; }
}
