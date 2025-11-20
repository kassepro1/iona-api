package com.iona.ionaapi.domain.entities;

/**
 * Type de recommandation IA.
 * Correspond à AiRecommendation.type du modèle TypeScript.
 */
public enum RecommendationType {
    /**
     * Recommandation d'optimisation
     */
    OPTIMIZATION("optimization", "Optimisation"),
    
    /**
     * Recommandation d'amélioration
     */
    IMPROVEMENT("improvement", "Amélioration"),
    
    /**
     * Information générale
     */
    INFORMATION("information", "Information");

    private final String code;
    private final String label;

    RecommendationType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static RecommendationType fromCode(String code) {
        for (RecommendationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de recommandation invalide: " + code);
    }
}