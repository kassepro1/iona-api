package com.iona.ionaapi.infrastructure.dto;

import com.iona.ionaapi.domain.partner.enums.PartnerStatus;
import com.iona.ionaapi.domain.partner.Address;

public class UpdatePartnerRequest {
    private String name;
    private String contactEmail;
    private String phone;
    private PartnerStatus status;
    private Address address;
    
    // Constructeurs
    public UpdatePartnerRequest() {}
    
    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public PartnerStatus getStatus() { return status; }
    public void setStatus(PartnerStatus status) { this.status = status; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}