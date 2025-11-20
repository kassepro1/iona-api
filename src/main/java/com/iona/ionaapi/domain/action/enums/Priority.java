package com.iona.ionaapi.domain.action.enums;

/**
 * Niveau de priorité pour les recommandations et actions.
 * Correspond aux champs priority des modèles TypeScript.
 */
public enum Priority {
    /**
     * Priorité basse
     */
    LOW("low", "Basse", 1),
    
    /**
     * Priorité moyenne
     */
    MEDIUM("medium", "Moyenne", 2),
    
    /**
     * Priorité haute
     */
    HIGH("high", "Haute", 3),
    
    /**
     * Priorité urgente (uniquement pour les actions)
     */
    URGENT("urgent", "Urgente", 4);

    private final String code;
    private final String label;
    private final int level;

    Priority(String code, String label, int level) {
        this.code = code;
        this.label = label;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    public static Priority fromCode(String code) {
        for (Priority priority : values()) {
            if (priority.code.equalsIgnoreCase(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Priorité invalide: " + code);
    }

    /**
     * Vérifie si la priorité est critique (HIGH ou URGENT)
     */
    public boolean isCritical() {
        return this == HIGH || this == URGENT;
    }

    /**
     * Compare deux priorités (pour le tri)
     */
    public boolean isHigherThan(Priority other) {
        return this.level > other.level;
    }
}