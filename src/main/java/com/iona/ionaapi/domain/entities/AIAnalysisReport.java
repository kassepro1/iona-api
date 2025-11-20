package com.iona.ionaapi.domain.entities;

import com.iona.ionaapi.domain.action.AiAction;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Entité représentant le rapport complet d'analyse IA d'une attestation.
 * Correspond à AiAnalysisReport du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "ai_analysis_reports")
public class AIAnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * ID de l'attestation analysée
     */
    @Column(name = "attestation_id", nullable = false)
    private UUID attestationId;

    /**
     * Date et heure de l'analyse
     */
    @Column(name = "analyzed_at", nullable = false)
    private Instant analyzedAt;

    /**
     * Statut global de conformité
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AiStatus status;

    /**
     * Score global de conformité (0-100)
     */
    @Column(name = "global_score", nullable = false)
    private Integer globalScore;

    /**
     * Données extraites du document
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "extracted_data_id", unique = true)
    private ExtractedData extractedData;

    /**
     * Recommandations générées
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analysis_report_id")
    private List<AiRecommendation> recommendations = new ArrayList<>();

    /**
     * Alertes détectées
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analysis_report_id")
    private List<AiAlert> alerts = new ArrayList<>();

    /**
     * Actions recommandées
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analysis_report_id")
    private List<AiAction> actions = new ArrayList<>();

    /**
     * Temps de traitement (en secondes)
     */
    @Column(name = "processing_time", nullable = false)
    private Integer processingTime;

    /**
     * Niveau de confiance de l'analyse (0-100)
     */
    @Column(name = "confidence", nullable = false)
    private Integer confidence;

    /**
     * Version du modèle IA utilisé
     */
    @Column(name = "version", nullable = false, length = 50)
    private String version;

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
    protected AIAnalysisReport() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public AIAnalysisReport(UUID attestationId, AiStatus status, Integer globalScore,
                           ExtractedData extractedData, Integer processingTime, 
                           Integer confidence, String version, String tenantId) {
        this.attestationId = Objects.requireNonNull(attestationId, "L'ID de l'attestation ne peut pas être nul");
        this.status = Objects.requireNonNull(status, "Le statut ne peut pas être nul");
        this.globalScore = Objects.requireNonNull(globalScore, "Le score global ne peut pas être nul");
        this.extractedData = Objects.requireNonNull(extractedData, "Les données extraites ne peuvent pas être nulles");
        this.processingTime = Objects.requireNonNull(processingTime, "Le temps de traitement ne peut pas être nul");
        this.confidence = Objects.requireNonNull(confidence, "La confiance ne peut pas être nulle");
        this.version = Objects.requireNonNull(version, "La version ne peut pas être nulle");
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        this.analyzedAt = Instant.now();
        this.createdAt = Instant.now();
        
        validateScore(globalScore);
        validateConfidence(confidence);
    }

    // Business methods

    /**
     * Ajoute une recommandation
     */
    public void addRecommendation(AiRecommendation recommendation) {
        if (!this.recommendations.contains(recommendation)) {
            this.recommendations.add(recommendation);
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Ajoute une alerte
     */
    public void addAlert(AiAlert alert) {
        if (!this.alerts.contains(alert)) {
            this.alerts.add(alert);
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Ajoute une action
     */
    public void addAction(AiAction action) {
        if (!this.actions.contains(action)) {
            this.actions.add(action);
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Retourne les alertes critiques non résolues
     */
    public List<AiAlert> getCriticalUnresolvedAlerts() {
        return alerts.stream()
            .filter(AiAlert::requiresImmediateAction)
            .collect(Collectors.toList());
    }

    /**
     * Retourne les actions urgentes actives
     */
    public List<AiAction> getUrgentActiveActions() {
        return actions.stream()
            .filter(AiAction::isUrgentAndActive)
            .collect(Collectors.toList());
    }

    /**
     * Retourne les recommandations prioritaires
     */
    public List<AiRecommendation> getPriorityRecommendations() {
        return recommendations.stream()
            .filter(AiRecommendation::isPriority)
            .collect(Collectors.toList());
    }

    /**
     * Compte le nombre d'alertes par sévérité
     */
    public long countAlertsBySeverity(Severity severity) {
        return alerts.stream()
            .filter(alert -> alert.getSeverity() == severity)
            .count();
    }

    /**
     * Vérifie si le rapport contient des problèmes critiques
     */
    public boolean hasCriticalIssues() {
        return !getCriticalUnresolvedAlerts().isEmpty();
    }

    /**
     * Vérifie si l'attestation est globalement conforme
     */
    public boolean isCompliant() {
        return status == AiStatus.COMPLIANT;
    }

    /**
     * Calcule un score de risque basé sur les alertes
     */
    public int calculateRiskScore() {
        int criticalCount = (int) countAlertsBySeverity(Severity.CRITICAL);
        int warningCount = (int) countAlertsBySeverity(Severity.WARNING);
        
        return (criticalCount * 30) + (warningCount * 10);
    }

    /**
     * Met à jour le statut global
     */
    public void updateStatus(AiStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    // Validation methods

    private void validateScore(Integer score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Le score doit être entre 0 et 100");
        }
    }

    private void validateConfidence(Integer confidence) {
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException("La confiance doit être entre 0 et 100");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getAttestationId() {
        return attestationId;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public AiStatus getStatus() {
        return status;
    }

    public Integer getGlobalScore() {
        return globalScore;
    }

    public ExtractedData getExtractedData() {
        return extractedData;
    }

    public List<AiRecommendation> getRecommendations() {
        return new ArrayList<>(recommendations);
    }

    public List<AiAlert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    public List<AiAction> getActions() {
        return new ArrayList<>(actions);
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public String getVersion() {
        return version;
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

    // Setters (pour updates)
    public void setGlobalScore(Integer globalScore) {
        validateScore(globalScore);
        this.globalScore = globalScore;
        this.updatedAt = Instant.now();
    }

    public void setExtractedData(ExtractedData extractedData) {
        this.extractedData = extractedData;
        this.updatedAt = Instant.now();
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }

    public void setConfidence(Integer confidence) {
        validateConfidence(confidence);
        this.confidence = confidence;
        this.updatedAt = Instant.now();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIAnalysisReport that = (AIAnalysisReport) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AIAnalysisReport{id=%s, attestationId=%s, status=%s, globalScore=%d, confidence=%d}", 
                           id, attestationId, status, globalScore, confidence);
    }
}