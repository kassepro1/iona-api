package com.iona.ionaapi.infrastructure.repository;

import com.iona.ionaapi.domain.partner.enums.PartnerStatus;
import com.iona.ionaapi.domain.partner.enums.PartnerType;
import com.iona.ionaapi.domain.partner.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    
    /**
     * Trouve un partner par SIRET
     */
    Optional<Partner> findBySiret(String siret);
    
    /**
     * Trouve des partners par type
     */
    List<Partner> findByPartnerType(PartnerType partnerType);
    
    /**
     * Trouve des partners par statut
     */
    List<Partner> findByStatus(PartnerStatus status);
    
    /**
     * Recherche de partners par nom (insensible à la casse)
     */
    @Query("SELECT p FROM Partner p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Partner> searchByName(@Param("name") String name);
    
    /**
     * Compte le nombre de partners par type
     */
    @Query("SELECT COUNT(p) FROM Partner p WHERE p.partnerType = :type")
    long countByPartnerType(@Param("type") PartnerType type);
    
    /**
     * Vérifie si un SIRET existe déjà
     */
    boolean existsBySiret(String siret);
}