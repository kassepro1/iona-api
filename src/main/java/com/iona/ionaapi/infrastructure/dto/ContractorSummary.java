package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.contractor.Contractor;
import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;

import java.util.Set;

/**
 * Contractor summary for lists (lighter version without full details)
 */
public class ContractorSummary {
    private String id;
    private String name;
    private ContractorType type;
    private Set<ContractorSpecialty> specialties;
    private String primaryContactName;
    private String primaryContactPhone;
    
    // Constructors
    public ContractorSummary() {}
    
    public ContractorSummary(Contractor contractor) {
        this.id = contractor.getId().toString();
        this.name = contractor.getName();
        this.type = contractor.getType();
        this.specialties = contractor.getSpecialties();
        
        var primaryContact = contractor.getPrimaryContact();
        if (primaryContact != null) {
            this.primaryContactName = primaryContact.getFullName();
            this.primaryContactPhone = primaryContact.getPhone();
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ContractorType getType() { return type; }
    public void setType(ContractorType type) { this.type = type; }
    
    public Set<ContractorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ContractorSpecialty> specialties) { this.specialties = specialties; }
    
    public String getPrimaryContactName() { return primaryContactName; }
    public void setPrimaryContactName(String primaryContactName) { this.primaryContactName = primaryContactName; }
    
    public String getPrimaryContactPhone() { return primaryContactPhone; }
    public void setPrimaryContactPhone(String primaryContactPhone) { this.primaryContactPhone = primaryContactPhone; }
}