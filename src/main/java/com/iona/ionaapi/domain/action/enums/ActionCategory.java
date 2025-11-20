package com.iona.ionaapi.domain.action.enums;

/**
 * Catégorie d'action.
 * Correspond à AiAction.category du modèle TypeScript.
 */
public enum ActionCategory {
    /**
     * Action relative à un document
     */
    DOCUMENT("document", "Document"),
    
    /**
     * Action de contact avec l'intervenant
     */
    CONTACT("contact", "Contact"),
    
    /**
     * Action de renouvellement
     */
    RENEWAL("renewal", "Renouvellement"),
    
    /**
     * Action de vérification
     */
    VERIFICATION("verification", "Vérification");

    private final String code;
    private final String label;

    ActionCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ActionCategory fromCode(String code) {
        for (ActionCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Catégorie d'action invalide: " + code);
    }

    /**
     * Vérifie si cette action nécessite une interaction humaine
     */
    public boolean requiresHumanInteraction() {
        return this == CONTACT || this == VERIFICATION;
    }

    /**
     * Vérifie si cette action peut être automatisée
     */
    public boolean canBeAutomated() {
        return this == DOCUMENT || this == RENEWAL;
    }
}