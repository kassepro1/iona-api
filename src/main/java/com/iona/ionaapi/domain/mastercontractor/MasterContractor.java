package com.iona.ionaapi.domain.mastercontractor;

import com.iona.ionaapi.domain.mastercontractor.enums.MasterContractorType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Master Contractor entity - owner of construction projects
 * This entity will be automatically created in the current tenant's schema
 */
@Entity
@Table(name = "master_contractors")
public class MasterContractor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MasterContractorType type;
    
    @Column(name = "siret", nullable = false)
    private String siret;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @OneToMany(mappedBy = "masterContractor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
    
    // Constructors
    public MasterContractor() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }
    
    public MasterContractor(String name, MasterContractorType type, String siret, String address) {
        this();
        this.name = name;
        this.type = type;
        this.siret = siret;
        this.address = address;
    }
    
    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        lastUpdatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = Instant.now();
    }
    
    // Utility methods to manage contacts
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setMasterContractor(this);
    }
    
    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setMasterContractor(null);
    }
    
    public Contact getPrimaryContact() {
        return contacts.stream()
                .filter(Contact::isPrimary)
                .findFirst()
                .orElse(null);
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public MasterContractorType getType() { return type; }
    public void setType(MasterContractorType type) { this.type = type; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public List<Contact> getContacts() { return contacts; }
    public void setContacts(List<Contact> contacts) { 
        this.contacts = contacts; 
    }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    
    @Override
    public String toString() {
        return String.format("MasterContractor{id=%s, name='%s', type=%s, siret='%s'}", 
                           id, name, type, siret);
    }
}