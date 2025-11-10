package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.partner.enums.PartnerType;
import com.iona.ionaapi.domain.partner.Address;

public class CreatePartnerRequest {
    private String name;
    private PartnerType partnerType;
    private String siret;
    private String contactEmail;
    private String phone;
    private Address address;
    
    // Constructeurs
    public CreatePartnerRequest() {}
    
    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public PartnerType getPartnerType() { return partnerType; }
    public void setPartnerType(PartnerType partnerType) { this.partnerType = partnerType; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}