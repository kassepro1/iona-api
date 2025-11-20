package com.iona.ionaapi.domain.entities;

import com.iona.ionaapi.domain.valueobjects.ValidityPeriod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant une couverture d'assurance.
 * Correspond à Coverage du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "coverages")
public class Coverage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type de couverture (ex: "Responsabilité Civile", "Décennale")
     */
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    /**
     * Description détaillée de la couverture
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Montant de la couverture (en euros)
     */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Indique si la couverture est actuellement valide
     */
    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;

    /**
     * Période de validité de la couverture
     */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "validity_start")),
        @AttributeOverride(name = "endDate", column = @Column(name = "validity_end"))
    })
    private ValidityPeriod validityPeriod;

    /**
     * Tenant ID pour isolation multi-tenant
     */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /**
     * Constructeur par défaut pour JPA
     */
    protected Coverage() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public Coverage(String type, String description, BigDecimal amount, 
                   Boolean isValid, ValidityPeriod validityPeriod, String tenantId) {
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être nul");
        this.description = description;
        this.amount = Objects.requireNonNull(amount, "Le montant ne peut pas être nul");
        this.isValid = Objects.requireNonNull(isValid, "isValid ne peut pas être nul");
        this.validityPeriod = Objects.requireNonNull(validityPeriod, "La période de validité ne peut pas être nulle");
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
    }

    // Business methods

    /**
     * Vérifie si le montant de couverture est suffisant par rapport à un seuil
     */
    public boolean isCoverageAmountSufficient(BigDecimal requiredAmount) {
        return amount.compareTo(requiredAmount) >= 0;
    }

    /**
     * Vérifie si la couverture est expirée
     */
    public boolean isExpired() {
        return validityPeriod.isExpired();
    }

    /**
     * Vérifie si l'expiration approche
     */
    public boolean isExpiryApproaching() {
        return validityPeriod.isExpiryApproaching();
    }

    /**
     * Marque la couverture comme invalide
     */
    public void markAsInvalid() {
        this.isValid = false;
    }

    /**
     * Marque la couverture comme valide
     */
    public void markAsValid() {
        this.isValid = true;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public String getTenantId() {
        return tenantId;
    }

    // Setters (pour JPA et updates)
    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coverage coverage = (Coverage) o;
        return Objects.equals(id, coverage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Coverage{id=%s, type='%s', amount=%s, isValid=%s}", 
                           id, type, amount, isValid);
    }
}