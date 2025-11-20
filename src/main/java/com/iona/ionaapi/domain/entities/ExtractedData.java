package com.iona.ionaapi.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant les données extraites d'une attestation par l'IA.
 * Correspond à ExtractedData du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "extracted_data")
public class ExtractedData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type de document (ex: "Attestation Responsabilité Civile Professionnelle")
     */
    @Column(name = "document_type", nullable = false)
    private String documentType;

    /**
     * Émetteur de l'attestation (ex: "Allianz France")
     */
    @Column(name = "issuer", nullable = false)
    private String issuer;

    /**
     * Date d'émission
     */
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /**
     * Date d'expiration
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    /**
     * Numéro de police
     */
    @Column(name = "policy_number", nullable = false, length = 100)
    private String policyNumber;

    /**
     * Montant total de couverture (en euros)
     */
    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;

    /**
     * Franchise (en euros)
     */
    @Column(name = "deductible", precision = 15, scale = 2)
    private BigDecimal deductible;

    /**
     * Prime (en euros)
     */
    @Column(name = "premium", precision = 15, scale = 2)
    private BigDecimal premium;

    /**
     * Sous-traitance autorisée
     */
    @Column(name = "subcontracting_allowed", nullable = false)
    private Boolean subcontractingAllowed = false;

    /**
     * Limite de sous-traitance (optionnelle, en euros)
     */
    @Column(name = "subcontracting_limit", precision = 15, scale = 2)
    private BigDecimal subcontractingLimit;

    /**
     * Couvertures détaillées
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "extracted_data_id")
    private List<Coverage> coverages = new ArrayList<>();

    /**
     * Exclusions
     */
    @ElementCollection
    @CollectionTable(name = "extracted_data_exclusions", 
                    joinColumns = @JoinColumn(name = "extracted_data_id"))
    @Column(name = "exclusion", columnDefinition = "TEXT")
    private List<String> exclusions = new ArrayList<>();

    /**
     * Activités couvertes
     */
    @ElementCollection
    @CollectionTable(name = "extracted_data_covered_activities", 
                    joinColumns = @JoinColumn(name = "extracted_data_id"))
    @Column(name = "activity")
    private List<String> coveredActivities = new ArrayList<>();

    /**
     * Zones géographiques couvertes
     */
    @ElementCollection
    @CollectionTable(name = "extracted_data_geographic_scope", 
                    joinColumns = @JoinColumn(name = "extracted_data_id"))
    @Column(name = "zone")
    private List<String> geographicScope = new ArrayList<>();

    /**
     * Tenant ID pour isolation multi-tenant
     */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /**
     * Constructeur par défaut pour JPA
     */
    protected ExtractedData() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public ExtractedData(String documentType, String issuer, LocalDate issueDate,
                        LocalDate expiryDate, String policyNumber, 
                        BigDecimal coverageAmount, Boolean subcontractingAllowed,
                        String tenantId) {
        this.documentType = Objects.requireNonNull(documentType, "Le type de document ne peut pas être nul");
        this.issuer = Objects.requireNonNull(issuer, "L'émetteur ne peut pas être nul");
        this.issueDate = Objects.requireNonNull(issueDate, "La date d'émission ne peut pas être nulle");
        this.expiryDate = Objects.requireNonNull(expiryDate, "La date d'expiration ne peut pas être nulle");
        this.policyNumber = Objects.requireNonNull(policyNumber, "Le numéro de police ne peut pas être nul");
        this.coverageAmount = Objects.requireNonNull(coverageAmount, "Le montant de couverture ne peut pas être nul");
        this.subcontractingAllowed = Objects.requireNonNull(subcontractingAllowed, "subcontractingAllowed ne peut pas être nul");
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        
        validateDates();
        validateCoverageAmount();
    }

    // Business methods

    /**
     * Vérifie si l'attestation est expirée
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Vérifie si l'expiration approche (moins de 60 jours)
     */
    public boolean isExpiryApproaching() {
        LocalDate now = LocalDate.now();
        return expiryDate.isAfter(now) && 
               expiryDate.isBefore(now.plusDays(60));
    }

    /**
     * Vérifie si une activité spécifique est couverte
     */
    public boolean isActivityCovered(String activity) {
        return coveredActivities.stream()
            .anyMatch(a -> a.toLowerCase().contains(activity.toLowerCase()));
    }

    /**
     * Vérifie si une zone géographique est couverte
     */
    public boolean isGeographicZoneCovered(String zone) {
        return geographicScope.stream()
            .anyMatch(z -> z.toLowerCase().contains(zone.toLowerCase()));
    }

    /**
     * Vérifie si le montant de couverture est suffisant
     */
    public boolean isCoverageAmountSufficient(BigDecimal requiredAmount) {
        return coverageAmount.compareTo(requiredAmount) >= 0;
    }

    /**
     * Ajoute une couverture
     */
    public void addCoverage(Coverage coverage) {
        if (!this.coverages.contains(coverage)) {
            this.coverages.add(coverage);
        }
    }

    /**
     * Ajoute une exclusion
     */
    public void addExclusion(String exclusion) {
        if (!this.exclusions.contains(exclusion)) {
            this.exclusions.add(exclusion);
        }
    }

    /**
     * Ajoute une activité couverte
     */
    public void addCoveredActivity(String activity) {
        if (!this.coveredActivities.contains(activity)) {
            this.coveredActivities.add(activity);
        }
    }

    /**
     * Ajoute une zone géographique
     */
    public void addGeographicZone(String zone) {
        if (!this.geographicScope.contains(zone)) {
            this.geographicScope.add(zone);
        }
    }

    // Validation methods

    private void validateDates() {
        if (expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("La date d'expiration doit être postérieure à la date d'émission");
        }
    }

    private void validateCoverageAmount() {
        if (coverageAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de couverture doit être positif");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getIssuer() {
        return issuer;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public Boolean getSubcontractingAllowed() {
        return subcontractingAllowed;
    }

    public BigDecimal getSubcontractingLimit() {
        return subcontractingLimit;
    }

    public List<Coverage> getCoverages() {
        return new ArrayList<>(coverages);
    }

    public List<String> getExclusions() {
        return new ArrayList<>(exclusions);
    }

    public List<String> getCoveredActivities() {
        return new ArrayList<>(coveredActivities);
    }

    public List<String> getGeographicScope() {
        return new ArrayList<>(geographicScope);
    }

    public String getTenantId() {
        return tenantId;
    }

    // Setters (pour updates)
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        validateDates();
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        validateDates();
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
        validateCoverageAmount();
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public void setSubcontractingAllowed(Boolean subcontractingAllowed) {
        this.subcontractingAllowed = subcontractingAllowed;
    }

    public void setSubcontractingLimit(BigDecimal subcontractingLimit) {
        this.subcontractingLimit = subcontractingLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtractedData that = (ExtractedData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ExtractedData{id=%s, documentType='%s', issuer='%s', policyNumber='%s'}", 
                           id, documentType, issuer, policyNumber);
    }
}