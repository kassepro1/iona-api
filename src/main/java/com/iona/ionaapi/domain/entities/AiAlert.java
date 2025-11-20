package com.iona.ionaapi.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant une alerte générée par l'IA.
 * Correspond à AiAlert du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "ai_alerts")
public class AiAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Niveau de sévérité
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private Severity severity;

    /**
     * Catégorie de l'alerte
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private AlertCategory category;

    /**
     * Titre de l'alerte
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Description détaillée
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Éléments affectés (ex: ["Couverture RC", "Zone géographique"])
     */
    @ElementCollection
    @CollectionTable(name = "alert_affected_items", 
                    joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "item")
    private List<String> affectedItems = new ArrayList<>();

    /**
     * Date de détection
     */
    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    /**
     * Indique si l'alerte a été résolue
     */
    @Column(name = "resolved", nullable = false)
    private Boolean resolved = false;

    /**
     * Date de résolution
     */
    @Column(name = "resolved_at")
    private Instant resolvedAt;

    /**
     * Tenant ID pour isolation multi-tenant
     */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /**
     * Constructeur par défaut pour JPA
     */
    protected AiAlert() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public AiAlert(Severity severity, AlertCategory category, String title,
                  String description, List<String> affectedItems, String tenantId) {
        this.severity = Objects.requireNonNull(severity, "La sévérité ne peut pas être nulle");
        this.category = Objects.requireNonNull(category, "La catégorie ne peut pas être nulle");
        this.title = Objects.requireNonNull(title, "Le titre ne peut pas être nul");
        this.description = description;
        this.affectedItems = affectedItems != null ? new ArrayList<>(affectedItems) : new ArrayList<>();
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        this.detectedAt = Instant.now();
        this.resolved = false;
    }

    // Business methods

    /**
     * Marque l'alerte comme résolue
     */
    public void markAsResolved() {
        if (resolved) {
            throw new IllegalStateException("L'alerte est déjà résolue");
        }
        this.resolved = true;
        this.resolvedAt = Instant.now();
    }

    /**
     * Rouvre une alerte résolue
     */
    public void reopen() {
        if (!resolved) {
            throw new IllegalStateException("L'alerte n'est pas résolue");
        }
        this.resolved = false;
        this.resolvedAt = null;
    }

    /**
     * Vérifie si l'alerte nécessite une action immédiate
     */
    public boolean requiresImmediateAction() {
        return severity.requiresImmediateAction() && !resolved;
    }

    /**
     * Vérifie si l'alerte concerne des aspects juridiques
     */
    public boolean isLegalIssue() {
        return category.isLegalIssue();
    }

    /**
     * Ajoute un élément affecté
     */
    public void addAffectedItem(String item) {
        if (!this.affectedItems.contains(item)) {
            this.affectedItems.add(item);
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Severity getSeverity() {
        return severity;
    }

    public AlertCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAffectedItems() {
        return new ArrayList<>(affectedItems);
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    // Setters (pour updates)
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setCategory(AlertCategory category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AiAlert aiAlert = (AiAlert) o;
        return Objects.equals(id, aiAlert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AiAlert{id=%s, severity=%s, category=%s, title='%s', resolved=%s}", 
                           id, severity, category, title, resolved);
    }
}