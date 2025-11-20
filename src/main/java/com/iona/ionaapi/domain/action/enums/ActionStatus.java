package com.iona.ionaapi.domain.action.enums;

/**
 * Statut d'une action.
 * Correspond à AiAction.status du modèle TypeScript.
 */
public enum ActionStatus {
    /**
     * Action en attente
     */
    PENDING("pending", "En attente"),
    
    /**
     * Action en cours
     */
    IN_PROGRESS("in-progress", "En cours"),
    
    /**
     * Action terminée
     */
    COMPLETED("completed", "Terminée"),
    
    /**
     * Action annulée/rejetée
     */
    DISMISSED("dismissed", "Annulée");

    private final String code;
    private final String label;

    ActionStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ActionStatus fromCode(String code) {
        for (ActionStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Statut d'action invalide: " + code);
    }

    /**
     * Vérifie si l'action est terminée (succès ou annulée)
     */
    public boolean isFinished() {
        return this == COMPLETED || this == DISMISSED;
    }

    /**
     * Vérifie si l'action est active (pending ou in progress)
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS;
    }
}