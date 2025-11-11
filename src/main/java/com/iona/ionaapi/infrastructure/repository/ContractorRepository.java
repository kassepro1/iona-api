package com.iona.ionaapi.infrastructure.repository;

import com.iona.ionaapi.domain.contractor.Contractor;

import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorStatus;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Contractor entity
 * All queries execute automatically in the current tenant's schema
 */
@Repository
public interface ContractorRepository extends JpaRepository<Contractor, UUID> {
    
    /**
     * Find contractor by SIRET
     */
    Optional<Contractor> findBySiret(String siret);
    
    /**
     * Find contractors by type
     */
    List<Contractor> findByType(ContractorType type);
    
    /**
     * Find contractors by status
     */
    List<Contractor> findByStatus(ContractorStatus status);
    
    /**
     * Search contractors by name (case insensitive)
     */
    @Query("SELECT c FROM Contractor c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Contractor> searchByName(@Param("name") String name);
    
    /**
     * Find contractors by specialty
     */
    @Query("SELECT c FROM Contractor c JOIN c.specialties s WHERE s = :specialty")
    List<Contractor> findBySpecialty(@Param("specialty") ContractorSpecialty specialty);
    
    /**
     * Find contractors with any of the given specialties
     */
    @Query("SELECT DISTINCT c FROM Contractor c JOIN c.specialties s WHERE s IN :specialties")
    List<Contractor> findBySpecialtiesIn(@Param("specialties") List<ContractorSpecialty> specialties);
    
    /**
     * Check if SIRET already exists
     */
    boolean existsBySiret(String siret);
    
    /**
     * Count contractors by type
     */
    @Query("SELECT COUNT(c) FROM Contractor c WHERE c.type = :type")
    long countByType(@Param("type") ContractorType type);
    
    /**
     * Count contractors by specialty
     */
    @Query("SELECT COUNT(DISTINCT c) FROM Contractor c JOIN c.specialties s WHERE s = :specialty")
    long countBySpecialty(@Param("specialty") ContractorSpecialty specialty);
    
    /**
     * Find active contractors only
     */
    @Query("SELECT c FROM Contractor c WHERE c.status = 'ACTIVE'")
    List<Contractor> findActiveContractors();
}