package com.iona.ionaapi.domain.entities;

/**
 * Statut de l'analyse IA d'une attestation.
 * Correspond au type AiStatus du modèle TypeScript frontend.
 * 
 * @author IONA Team
 * @version 1.0
 */
public enum AiStatus {
    /**
     * Attestation uploadée, en attente d'analyse
     */
    PENDING("pending", "En attente"),
    
    /**
     * Analyse en cours
     */
    ANALYZING("analyzing", "Analyse en cours"),
    
    /**
     * Attestation conforme aux exigences légales
     */
    COMPLIANT("compliant", "Conforme"),
    
    /**
     * Attestation non conforme
     */
    NON_COMPLIANT("non-compliant", "Non conforme"),
    
    /**
     * Erreur lors de l'analyse
     */
    ERROR("error", "Erreur");

    private final String code;
    private final String label;

    AiStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Convertit un code string en enum AiStatus
     * @param code le code à convertir (ex: "pending", "compliant")
     * @return l'enum correspondant
     * @throws IllegalArgumentException si le code est invalide
     */
    public static AiStatus fromCode(String code) {
        for (AiStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Code AiStatus invalide: " + code);
    }

    /**
     * Vérifie si l'analyse est terminée (succès ou échec)
     */
    public boolean isAnalysisCompleted() {
        return this == COMPLIANT || this == NON_COMPLIANT || this == ERROR;
    }

    /**
     * Vérifie si l'analyse est en cours ou en attente
     */
    public boolean isAnalysisPending() {
        return this == PENDING || this == ANALYZING;
    }
}