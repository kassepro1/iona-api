package com.iona.ionaapi.domain.entities;

/**
 * Niveau de sévérité d'une alerte.
 * Correspond à AiAlert.severity du modèle TypeScript.
 */
public enum Severity {
    /**
     * Information - pas de risque
     */
    INFO("info", "Information", 1),
    
    /**
     * Avertissement - attention requise
     */
    WARNING("warning", "Avertissement", 2),
    
    /**
     * Critique - action immédiate requise
     */
    CRITICAL("critical", "Critique", 3);

    private final String code;
    private final String label;
    private final int level;

    Severity(String code, String label, int level) {
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

    public static Severity fromCode(String code) {
        for (Severity severity : values()) {
            if (severity.code.equalsIgnoreCase(code)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Sévérité invalide: " + code);
    }

    /**
     * Vérifie si l'alerte nécessite une action immédiate
     */
    public boolean requiresImmediateAction() {
        return this == CRITICAL;
    }

    /**
     * Compare deux sévérités
     */
    public boolean isMoreSevereThan(Severity other) {
        return this.level > other.level;
    }
}