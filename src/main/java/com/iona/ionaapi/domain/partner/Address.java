package com.iona.ionaapi.domain.partner;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    
    @Column(name = "street")
    private String street;
    
    @Column(name = "postal_code") 
    private String postalCode;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "country")
    private String country;
    
    // Constructeurs
    public Address() {}
    
    public Address(String street, String postalCode, String city, String country) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }
    
    // Getters et Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    @Override
    public String toString() {
        return String.format("Address{street='%s', postalCode='%s', city='%s', country='%s'}", 
                           street, postalCode, city, country);
    }
}