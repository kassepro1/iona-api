package com.iona.ionaapi.domain.project.enums;

public enum AIAnalysisStatus {
    PENDING,        // En attente d'analyse
    IN_PROGRESS,    // Analyse en cours
    COMPLIANT,      // Conforme
    NON_COMPLIANT,  // Non conforme
    PARTIAL,        // Partiellement conforme
    ERROR          // Erreur d'analyse
}