package com.iona.ionaapi.infrastructure.dto;

/**
 * Contractor contact request
 */
public class ContractorContactRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private boolean isPrimary = false;
    
    // Constructors
    public ContractorContactRequest() {}
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
}