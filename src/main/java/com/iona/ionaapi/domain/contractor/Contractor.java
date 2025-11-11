package com.iona.ionaapi.domain.contractor;

import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorStatus;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

/**
 * Contractor entity - company that works on construction projects
 * This entity will be automatically created in the current tenant's schema
 */
@Entity
@Table(name = "contractors")
public class Contractor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ContractorType type;
    
    @Column(name = "siret", nullable = false)
    private String siret;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @ElementCollection(targetClass = ContractorSpecialty.class)
    @CollectionTable(name = "contractor_specialties", joinColumns = @JoinColumn(name = "contractor_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty")
    private Set<ContractorSpecialty> specialties = new HashSet<>();
    
    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContractorContact> contacts = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractorStatus status = ContractorStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
    
    // Constructors
    public Contractor() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }
    
    public Contractor(String name, ContractorType type, String siret, String address) {
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
    public void addContact(ContractorContact contact) {
        contacts.add(contact);
        contact.setContractor(this);
    }
    
    public void removeContact(ContractorContact contact) {
        contacts.remove(contact);
        contact.setContractor(null);
    }
    
    public ContractorContact getPrimaryContact() {
        return contacts.stream()
                .filter(ContractorContact::isPrimary)
                .findFirst()
                .orElse(null);
    }
    
    // Utility methods to manage specialties
    public void addSpecialty(ContractorSpecialty specialty) {
        specialties.add(specialty);
    }
    
    public void removeSpecialty(ContractorSpecialty specialty) {
        specialties.remove(specialty);
    }
    
    public boolean hasSpecialty(ContractorSpecialty specialty) {
        return specialties.contains(specialty);
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ContractorType getType() { return type; }
    public void setType(ContractorType type) { this.type = type; }
    
    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Set<ContractorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ContractorSpecialty> specialties) { 
        this.specialties = specialties; 
    }
    
    public List<ContractorContact> getContacts() { return contacts; }
    public void setContacts(List<ContractorContact> contacts) { 
        this.contacts = contacts; 
    }
    
    public ContractorStatus getStatus() { return status; }
    public void setStatus(ContractorStatus status) { this.status = status; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    
    @Override
    public String toString() {
        return String.format("Contractor{id=%s, name='%s', type=%s, siret='%s', specialties=%s}", 
                           id, name, type, siret, specialties);
    }
}