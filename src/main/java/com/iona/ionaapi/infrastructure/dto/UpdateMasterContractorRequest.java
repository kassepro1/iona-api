package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.mastercontractor.enums.MasterContractorType;

import java.util.List;

/**
 * Master contractor update request
 */
public class UpdateMasterContractorRequest {
    private String name;
    private MasterContractorType type;
    private String siret;
    private String address;
    private List<ContactRequest> contacts;

    // Constructors
    public UpdateMasterContractorRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MasterContractorType getType() { return type; }
    public void setType(MasterContractorType type) { this.type = type; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<ContactRequest> getContacts() { return contacts; }
    public void setContacts(List<ContactRequest> contacts) { this.contacts = contacts; }
}
