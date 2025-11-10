package com.iona.ionaapi.domain.partner;

import com.iona.ionaapi.domain.partner.enums.PartnerStatus;
import com.iona.ionaapi.domain.partner.enums.PartnerType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entité Partner - exemple d'utilisation du multi-tenant
 * Cette entité sera automatiquement créée dans le schema du tenant courant
 */
@Entity
@Table(name = "partners")
public class Partner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType partnerType;
    
    @Column(name = "siret", nullable = false)
    private String siret;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "phone")
    private String phone;
    
    @Embedded
    private Address address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartnerStatus status = PartnerStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
    
    // Constructeurs
    public Partner() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }
    
    public Partner(String name, PartnerType partnerType, String siret, String contactEmail) {
        this();
        this.name = name;
        this.partnerType = partnerType;
        this.siret = siret;
        this.contactEmail = contactEmail;
    }
    
    // Méthodes de cycle de vie JPA
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
    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
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
    
    public PartnerStatus getStatus() { return status; }
    public void setStatus(PartnerStatus status) { this.status = status; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    
    @Override
    public String toString() {
        return String.format("Partner{id=%s, name='%s', type=%s, siret='%s'}", 
                           id, name, partnerType, siret);
    }
}
