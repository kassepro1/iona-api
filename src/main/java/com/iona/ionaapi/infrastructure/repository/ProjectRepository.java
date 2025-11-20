package com.iona.ionaapi.infrastructure.repository;

import com.iona.ionaapi.domain.project.Project;
import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Project entity
 * All queries execute automatically in the current tenant's schema
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    /**
     * Find projects by owner (master contractor)
     */
    List<Project> findByOwnerId(UUID ownerId);

    List<Project> findAllByOwnerId(UUID ownerId);

    /**
     * Find projects by status
     */
    List<Project> findByStatus(ProjectStatus status);
    
    /**
     * Find projects by AI analysis status
     */
    @Query("SELECT p FROM Project p WHERE p.aiAnalysis.status = :status")
    List<Project> findByAIAnalysisStatus(@Param("status") AIAnalysisStatus status);
    
    /**
     * Search projects by name (case insensitive)
     */
    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Project> searchByName(@Param("name") String name);
    
    /**
     * Find projects with opening date in range
     */
    @Query("SELECT p FROM Project p WHERE p.openingDate BETWEEN :startDate AND :endDate")
    List<Project> findByOpeningDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find projects with specific contractor
     */
    @Query("SELECT p FROM Project p JOIN p.projectContractors pc WHERE pc.contractorId = :contractorId")
    List<Project> findByContractorId(@Param("contractorId") UUID contractorId);
    
    /**
     * Find active projects (STARTED or IN_PROGRESS)
     */
    @Query("SELECT p FROM Project p WHERE p.status IN ('STARTED', 'IN_PROGRESS')")
    List<Project> findActiveProjects();
    
    /**
     * Find projects needing AI analysis
     */
    @Query("SELECT p FROM Project p WHERE p.aiAnalysis.status IN ('PENDING', 'NON_COMPLIANT')")
    List<Project> findProjectsNeedingAnalysis();
    
    /**
     * Count projects by status
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(@Param("status") ProjectStatus status);
    
    /**
     * Count projects by AI status
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.aiAnalysis.status = :status")
    long countByAIStatus(@Param("status") AIAnalysisStatus status);
}