package com.iona.ionaapi.infrastructure.repository;

import com.iona.ionaapi.domain.mastercontractor.MasterContractor;
import com.iona.ionaapi.domain.mastercontractor.enums.MasterContractorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for MasterContractor entity
 * All queries execute automatically in the current tenant's schema
 */
@Repository
public interface MasterContractorRepository extends JpaRepository<MasterContractor, UUID> {
    
    /**
     * Find master contractor by SIRET
     */
    Optional<MasterContractor> findBySiret(String siret);
    
    /**
     * Find master contractors by type
     */
    List<MasterContractor> findByType(MasterContractorType type);
    
    /**
     * Search master contractors by name (case insensitive)
     */
    @Query("SELECT mc FROM MasterContractor mc WHERE LOWER(mc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MasterContractor> searchByName(@Param("name") String name);
    
    /**
     * Check if SIRET already exists
     */
    boolean existsBySiret(String siret);
    
    /**
     * Count master contractors by type
     */
    @Query("SELECT COUNT(mc) FROM MasterContractor mc WHERE mc.type = :type")
    long countByType(@Param("type") MasterContractorType type);
}