package com.iona.ionaapi.domain.entities;

import com.iona.ionaapi.domain.action.enums.Priority;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant une recommandation générée par l'IA.
 * Correspond à AiRecommendation du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "ai_recommendations")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type de recommandation
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private RecommendationType type;

    /**
     * Niveau de priorité
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    private Priority priority;

    /**
     * Titre de la recommandation
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Description détaillée
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Impact potentiel
     */
    @Column(name = "impact", columnDefinition = "TEXT")
    private String impact;

    /**
     * Coût estimé de mise en œuvre (optionnel)
     */
    @Column(name = "estimated_cost", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Date de création
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Tenant ID pour isolation multi-tenant
     */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /**
     * Constructeur par défaut pour JPA
     */
    protected AiRecommendation() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public AiRecommendation(RecommendationType type, Priority priority, String title,
                           String description, String impact, String tenantId) {
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être nul");
        this.priority = Objects.requireNonNull(priority, "La priorité ne peut pas être nulle");
        this.title = Objects.requireNonNull(title, "Le titre ne peut pas être nul");
        this.description = description;
        this.impact = impact;
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        this.createdAt = Instant.now();
    }

    // Business methods

    /**
     * Vérifie si la recommandation est prioritaire
     */
    public boolean isPriority() {
        return priority.isCritical();
    }

    /**
     * Vérifie si la recommandation a un coût estimé
     */
    public boolean hasCost() {
        return estimatedCost != null && estimatedCost.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Définit le coût estimé
     */
    public void setEstimatedCost(BigDecimal cost) {
        if (cost != null && cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le coût ne peut pas être négatif");
        }
        this.estimatedCost = cost;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public RecommendationType getType() {
        return type;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImpact() {
        return impact;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    // Setters (pour updates)
    public void setType(RecommendationType type) {
        this.type = type;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AiRecommendation that = (AiRecommendation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AiRecommendation{id=%s, type=%s, priority=%s, title='%s'}", 
                           id, type, priority, title);
    }
}