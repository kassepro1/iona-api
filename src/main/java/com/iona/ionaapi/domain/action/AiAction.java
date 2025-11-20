package com.iona.ionaapi.domain.action;


import com.iona.ionaapi.domain.action.enums.ActionCategory;
import com.iona.ionaapi.domain.action.enums.ActionStatus;
import com.iona.ionaapi.domain.action.enums.Priority;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant une action recommandée par l'IA.
 * Correspond à AiAction du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Entity
@Table(name = "ai_actions")
public class AiAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Statut de l'action
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ActionStatus status;

    /**
     * Niveau de priorité
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    private Priority priority;

    /**
     * Catégorie de l'action
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ActionCategory category;

    /**
     * Titre de l'action
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Description détaillée
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Date d'échéance (optionnelle)
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Personne assignée (optionnel)
     */
    @Column(name = "assigned_to")
    private String assignedTo;

    /**
     * Date de complétion
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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
    protected AiAction() {
    }

    /**
     * Constructeur avec tous les champs requis
     */
    public AiAction(ActionStatus status, Priority priority, ActionCategory category,
                   String title, String description, String tenantId) {
        this.status = Objects.requireNonNull(status, "Le statut ne peut pas être nul");
        this.priority = Objects.requireNonNull(priority, "La priorité ne peut pas être nulle");
        this.category = Objects.requireNonNull(category, "La catégorie ne peut pas être nulle");
        this.title = Objects.requireNonNull(title, "Le titre ne peut pas être nul");
        this.description = description;
        this.tenantId = Objects.requireNonNull(tenantId, "Le tenant ID ne peut pas être nul");
        this.createdAt = Instant.now();
    }

    // Business methods

    /**
     * Marque l'action comme commencée
     */
    public void start() {
        if (status != ActionStatus.PENDING) {
            throw new IllegalStateException("Seule une action en attente peut être démarrée");
        }
        this.status = ActionStatus.IN_PROGRESS;
    }

    /**
     * Marque l'action comme terminée
     */
    public void complete(String completionNotes) {
        if (status == ActionStatus.COMPLETED) {
            throw new IllegalStateException("L'action est déjà terminée");
        }
        if (status == ActionStatus.DISMISSED) {
            throw new IllegalStateException("L'action a été annulée");
        }
        this.status = ActionStatus.COMPLETED;
        this.completedAt = Instant.now();
        if (completionNotes != null) {
            this.notes = completionNotes;
        }
    }

    /**
     * Annule l'action
     */
    public void dismiss(String reason) {
        if (status.isFinished()) {
            throw new IllegalStateException("Une action terminée ne peut pas être annulée");
        }
        this.status = ActionStatus.DISMISSED;
        this.completedAt = Instant.now();
        this.notes = reason;
    }

    /**
     * Assigne l'action à une personne
     */
    public void assignTo(String person) {
        this.assignedTo = Objects.requireNonNull(person, "La personne ne peut pas être nulle");
    }

    /**
     * Définit la date d'échéance
     */
    public void setDueDate(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'échéance ne peut pas être dans le passé");
        }
        this.dueDate = dueDate;
    }

    /**
     * Vérifie si l'action est en retard
     */
    public boolean isOverdue() {
        return dueDate != null && 
               LocalDate.now().isAfter(dueDate) && 
               status.isActive();
    }

    /**
     * Vérifie si l'action est urgente et active
     */
    public boolean isUrgentAndActive() {
        return priority == Priority.URGENT && status.isActive();
    }

    /**
     * Vérifie si l'action nécessite une interaction humaine
     */
    public boolean requiresHumanInteraction() {
        return category.requiresHumanInteraction();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public ActionCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    // Setters additionnels
    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCategory(ActionCategory category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AiAction aiAction = (AiAction) o;
        return Objects.equals(id, aiAction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AiAction{id=%s, status=%s, priority=%s, title='%s'}", 
                           id, status, priority, title);
    }
}