package com.iona.ionaapi.domain.entities;

/**
 * Type d'attestation d'assurance.
 * Correspond au type AttestationType du modèle TypeScript.
 */
public enum AttestationType {
    /**
     * Responsabilité Civile Professionnelle
     */
    RC_PRO("RC_PRO", "Responsabilité Civile Professionnelle"),
    
    /**
     * Assurance Décennale
     */
    DECENNALE("DECENNALE", "Assurance Décennale"),
    
    /**
     * Assurance Auto/Mission
     */
    AUTO("AUTO", "Assurance Auto");

    private final String code;
    private final String label;

    AttestationType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static AttestationType fromCode(String code) {
        for (AttestationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'attestation invalide: " + code);
    }

    /**
     * Vérifie si ce type d'attestation est obligatoire pour les travaux de construction
     */
    public boolean isMandatoryForConstruction() {
        return this == RC_PRO || this == DECENNALE;
    }
}