package com.iona.ionaapi.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root représentant une attestation d'assurance.
 * Correspond à Attestation du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "attestations", indexes = {
    @Index(name = "idx_attestation_intervenant", columnList = "intervenant_id"),
    @Index(name = "idx_attestation_status", columnList = "ai_status"),
    @Index(name = "idx_attestation_tenant", columnList = "tenant_id")
})
public class Attestation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nom du fichier
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * Date d'upload
     */
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    /**
     * Statut de l'analyse IA
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_status", nullable = false, length = 50)
    private AiStatus aiStatus;

    /**
     * URL présignée S3 pour accéder au fichier
     */
    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    /**
     * Clé S3 du fichier (pour usage interne, non exposé au frontend)
     */
    @Column(name = "s3_object_key", length = 500)
    private String s3ObjectKey;

    /**
     * ID de l'intervenant propriétaire
     */
    @Column(name = "intervenant_id", nullable = false)
    private UUID intervenantId;

    /**
     * Type d'attestation
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AttestationType type;

    /**
     * Taille du fichier (en octets)
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * Nom de l'utilisateur ayant uploadé le document
     */
    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    /**
     * Pourcentage de progression de l'analyse IA (0-100)
     */
    @Column(name = "ai_progress_percent", nullable = false)
    private Integer aiProgressPercent = 0;

    /**
     * Indique si l'attestation est valide
     */
    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = false;

    /**
     * Date d'expiration de l'attestation
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Tags associés
     */
    @ElementCollection
    @CollectionTable(name = "attestation_tags", 
                    joinColumns = @JoinColumn(name = "attestation_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    /**
     * Rapport d'analyse IA (optionnel, créé après l'analyse)
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ai_analysis_report_id", unique = true)
    private AIAnalysisReport aiAnalysisReport;

    /**
     * Tenant ID pour isolation multi-tenant
     */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /**
     * Date de création
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Date de dernière modification
     */
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Constructeur par défaut pour JPA
     */
    protected Attestation() {
    }

    /**
     * Constructeur pour création d'une nouvelle attestation
     */
    public Attestation(String fileName, UUID intervenantId, AttestationType type,
                      Long fileSize, String uploadedBy, String s3ObjectKey, String tenantId) {
        this.fileName = Objects.requireNonNull(fileName, "Le nom du fichier ne peut pas être nul");
        this.intervenantId = Objects.requireNonNull(intervenantId, "L'ID de l'intervenant ne peut pas être nul");
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être nul");
        this.fileSize = Objects.requireNonNull(fileSize, "La taille du fichier ne peut pas être nulle");
        this.uploadedBy = Objects.requireNonNull(uploadedBy, "L'utilisateur ayant uploadé ne peut pas être nul");
        this.s3ObjectKey = Objects.requireNonNull(s3ObjectKey, "La clé S3 ne peut pas être nulle");
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        
        this.uploadedAt = Instant.now();
        this.aiStatus = AiStatus.PENDING;
        this.aiProgressPercent = 0;
        this.isValid = false;
        this.createdAt = Instant.now();
        
        validateFileSize();
    }

    // Business methods - Cycle de vie de l'analyse

    /**
     * Démarre l'analyse IA
     */
    public void startAnalysis() {
        if (aiStatus != AiStatus.PENDING && aiStatus != AiStatus.ERROR) {
            throw new IllegalStateException("L'analyse ne peut démarrer que depuis l'état PENDING ou ERROR");
        }
        this.aiStatus = AiStatus.ANALYZING;
        this.aiProgressPercent = 0;
        this.updatedAt = Instant.now();
    }

    /**
     * Met à jour la progression de l'analyse
     */
    public void updateAnalysisProgress(Integer progressPercent) {
        if (aiStatus != AiStatus.ANALYZING) {
            throw new IllegalStateException("La progression ne peut être mise à jour que pendant l'analyse");
        }
        if (progressPercent < 0 || progressPercent > 100) {
            throw new IllegalArgumentException("Le pourcentage doit être entre 0 et 100");
        }
        this.aiProgressPercent = progressPercent;
        this.updatedAt = Instant.now();
    }

    /**
     * Marque l'analyse comme terminée et attache le rapport
     */
    public void completeAnalysis(AIAnalysisReport report) {
        Objects.requireNonNull(report, "Le rapport ne peut pas être nul");
        
        if (aiStatus != AiStatus.ANALYZING) {
            throw new IllegalStateException("L'analyse doit être en cours pour être terminée");
        }
        
        this.aiAnalysisReport = report;
        this.aiStatus = report.getStatus();
        this.aiProgressPercent = 100;
        this.isValid = report.isCompliant();
        
        // Extraire la date d'expiration du rapport si disponible
        if (report.getExtractedData() != null) {
            this.expiryDate = report.getExtractedData().getExpiryDate();
        }
        
        this.updatedAt = Instant.now();
    }

    /**
     * Marque l'analyse comme en erreur
     */
    public void markAnalysisAsFailed(String errorMessage) {
        this.aiStatus = AiStatus.ERROR;
        this.updatedAt = Instant.now();
        // Note: errorMessage pourrait être stocké dans un champ dédié si nécessaire
    }

    // Business methods - Validation

    /**
     * Vérifie si l'attestation est expirée
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Vérifie si l'expiration approche (moins de 60 jours)
     */
    public boolean isExpiryApproaching() {
        if (expiryDate == null) return false;
        LocalDate now = LocalDate.now();
        return expiryDate.isAfter(now) && expiryDate.isBefore(now.plusDays(60));
    }

    /**
     * Vérifie si l'analyse est terminée
     */
    public boolean isAnalysisCompleted() {
        return aiStatus.isAnalysisCompleted();
    }

    /**
     * Vérifie si l'attestation a un rapport d'analyse
     */
    public boolean hasAnalysisReport() {
        return aiAnalysisReport != null;
    }

    // Business methods - Tags

    /**
     * Ajoute un tag
     */
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Supprime un tag
     */
    public void removeTag(String tag) {
        if (this.tags.remove(tag)) {
            this.updatedAt = Instant.now();
        }
    }

    // Business methods - URL

    /**
     * Met à jour l'URL présignée S3
     */
    public void updateFileUrl(String presignedUrl) {
        this.fileUrl = presignedUrl;
        this.updatedAt = Instant.now();
    }

    // Validation methods

    private void validateFileSize() {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("La taille du fichier doit être positive");
        }
        // Limite: 50 MB
        long maxSize = 50L * 1024 * 1024;
        if (fileSize > maxSize) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale autorisée de 50MB");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public AiStatus getAiStatus() {
        return aiStatus;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    public UUID getIntervenantId() {
        return intervenantId;
    }

    public AttestationType getType() {
        return type;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public Integer getAiProgressPercent() {
        return aiProgressPercent;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public AIAnalysisReport getAiAnalysisReport() {
        return aiAnalysisReport;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Setters (limités, la plupart des changements passent par les business methods)
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.updatedAt = Instant.now();
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attestation that = (Attestation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Attestation{id=%s, fileName='%s', type=%s, aiStatus=%s, isValid=%s}", 
                           id, fileName, type, aiStatus, isValid);
    }
}