package com.iona.ionaapi.domain.mastercontractor;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Contact entity - contact person for master contractor
 */
@Entity
@Table(name = "contacts")
class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "position")
    private String position;
    
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_contractor_id")
    private MasterContractor masterContractor;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
    
    // Constructors
    public Contact() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }
    
    public Contact(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
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
    
    public MasterContractor getMasterContractor() { return masterContractor; }
    public void setMasterContractor(MasterContractor masterContractor) { this.masterContractor = masterContractor; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    
    @Override
    public String toString() {
        return String.format("Contact{id=%s, fullName='%s', email='%s', isPrimary=%b}", 
                           id, getFullName(), email, isPrimary);
    }
}