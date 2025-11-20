package com.iona.ionaapi.domain.entities;

/**
 * Catégorie d'alerte IA.
 * Correspond à AiAlert.category du modèle TypeScript.
 */
public enum AlertCategory {
    /**
     * Problème de couverture d'assurance
     */
    COVERAGE("coverage", "Couverture"),
    
    /**
     * Problème d'expiration
     */
    EXPIRY("expiry", "Expiration"),
    
    /**
     * Problème de conformité légale
     */
    COMPLIANCE("compliance", "Conformité"),
    
    /**
     * Problème de montant de couverture
     */
    AMOUNT("amount", "Montant"),
    
    /**
     * Problème d'exclusion
     */
    EXCLUSION("exclusion", "Exclusion");

    private final String code;
    private final String label;

    AlertCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static AlertCategory fromCode(String code) {
        for (AlertCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Catégorie d'alerte invalide: " + code);
    }

    /**
     * Vérifie si cette catégorie concerne la validité juridique
     */
    public boolean isLegalIssue() {
        return this == COMPLIANCE || this == EXPIRY;
    }

    /**
     * Vérifie si cette catégorie concerne les aspects financiers
     */
    public boolean isFinancialIssue() {
        return this == AMOUNT || this == COVERAGE;
    }
}