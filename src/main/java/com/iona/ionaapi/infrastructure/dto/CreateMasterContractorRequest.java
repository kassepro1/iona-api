package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.mastercontractor.enums.MasterContractorType;

/**
 * Master contractor creation request
 */
public class CreateMasterContractorRequest {
    private String name;
    private MasterContractorType type;
    private String siret;
    private String address;
    
    // Constructors
    public CreateMasterContractorRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public MasterContractorType getType() { return type; }
    public void setType(MasterContractorType type) { this.type = type; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}