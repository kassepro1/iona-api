package com.iona.ionaapi.domain.valueobjects;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object représentant une période de validité.
 * Utilisé notamment dans les couvertures d'assurance.
 * Correspond à Coverage.validityPeriod du modèle TypeScript.
 * 
 * @author IONA Team
 */
@Embeddable
public class ValidityPeriod {
    
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructeur par défaut pour JPA
     */
    protected ValidityPeriod() {
    }

    /**
     * Constructeur avec validation
     */
    public ValidityPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être nulle");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("La date de fin ne peut pas être nulle");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Crée une période de validité à partir d'une date de début et d'une durée en mois
     */
    public static ValidityPeriod fromStartAndDuration(LocalDate startDate, int durationInMonths) {
        LocalDate endDate = startDate.plusMonths(durationInMonths);
        return new ValidityPeriod(startDate, endDate);
    }

    /**
     * Vérifie si une date est dans la période de validité
     */
    public boolean isDateInPeriod(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Vérifie si la période est actuellement valide
     */
    public boolean isCurrentlyValid() {
        return isDateInPeriod(LocalDate.now());
    }

    /**
     * Vérifie si la période est expirée
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Calcule le nombre de jours restants jusqu'à expiration
     * @return nombre de jours (négatif si expiré)
     */
    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * Vérifie si l'expiration approche (moins de 60 jours)
     */
    public boolean isExpiryApproaching() {
        long daysUntilExpiry = getDaysUntilExpiry();
        return daysUntilExpiry > 0 && daysUntilExpiry <= 60;
    }

    /**
     * Calcule la durée totale en mois
     */
    public long getDurationInMonths() {
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * Vérifie si deux périodes se chevauchent
     */
    public boolean overlaps(ValidityPeriod other) {
        return !this.endDate.isBefore(other.startDate) && !other.endDate.isBefore(this.startDate);
    }

    // Getters
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Equals & HashCode (Value Object - égalité par valeur)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidityPeriod that = (ValidityPeriod) o;
        return Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return String.format("ValidityPeriod{du %s au %s}", startDate, endDate);
    }
}