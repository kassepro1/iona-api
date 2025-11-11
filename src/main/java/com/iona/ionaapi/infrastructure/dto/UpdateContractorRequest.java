package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;

import java.util.List;
import java.util.Set;

/**
 * Contractor update request
 */
public class UpdateContractorRequest {
    private String name;
    private ContractorType type;
    private String siret;
    private String address;
    private Set<ContractorSpecialty> specialties;
    private List<ContractorContactRequest> contacts;
    
    // Constructors
    public UpdateContractorRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ContractorType getType() { return type; }
    public void setType(ContractorType type) { this.type = type; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Set<ContractorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ContractorSpecialty> specialties) { this.specialties = specialties; }
    
    public List<ContractorContactRequest> getContacts() { return contacts; }
    public void setContacts(List<ContractorContactRequest> contacts) { this.contacts = contacts; }
}