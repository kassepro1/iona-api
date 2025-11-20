package com.iona.ionaapi.application.service;

import com.iona.ionaapi.domain.project.Project;
import com.iona.ionaapi.domain.project.ProjectAIAnalysis;
import com.iona.ionaapi.domain.project.enums.AIAnalysisStatus;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;
import com.iona.ionaapi.infrastructure.repository.ProjectRepository;
import com.iona.ionaapi.infrastructure.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Business service for Project management
 * All operations are performed in the current tenant context
 */
@Service
@Transactional
public class ProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    private final ProjectRepository projectRepository;
    
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    /**
     * Creates a new project
     */
    public Project createProject(Project project) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Creating project '{}' for tenant: {}", project.getName(), tenant);
        
        // Validate owner exists (in a real app, we'd check with MasterContractorService)
        if (project.getOwnerId() == null) {
            throw new IllegalArgumentException("Project must have an owner (master contractor)");
        }
        
        // Set default values
        if (project.getStatus() == null) {
            project.setStatus(ProjectStatus.PLANNED);
        }
        
        // Initialize AI analysis if not set
        if (project.getAiAnalysis() == null) {
            project.setAiAnalysis(new ProjectAIAnalysis());
        }
        
        Project savedProject = projectRepository.save(project);
        logger.info("Project created with ID: {} for tenant: {}", savedProject.getId(), tenant);
        
        return savedProject;
    }
    
    /**
     * Updates an existing project
     */
    public Project updateProject(UUID id, Project project) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Updating project {} for tenant: {}", id, tenant);
        
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isEmpty()) {
            throw new IllegalArgumentException("Project not found: " + id);
        }
        
        Project toUpdate = existingProject.get();
        
        // Update basic fields
        if (project.getName() != null) {
            toUpdate.setName(project.getName());
        }
        if (project.getAddress() != null) {
            toUpdate.setAddress(project.getAddress());
        }
        if (project.getCost() != null) {
            toUpdate.setCost(project.getCost());
        }
        if (project.getOpeningDate() != null) {
            toUpdate.setOpeningDate(project.getOpeningDate());
        }
        if (project.getMission() != null) {
            toUpdate.setMission(project.getMission());
        }
        if (project.getStatus() != null) {
            toUpdate.setStatus(project.getStatus());
        }
        if (project.getOwnerId() != null) {
            toUpdate.setOwnerId(project.getOwnerId());
        }
        
        // Update project contractors if provided
        if (project.getProjectContractors() != null) {
            toUpdate.getProjectContractors().clear();
            toUpdate.getProjectContractors().addAll(project.getProjectContractors());
            
            // Set the project reference for each contractor
            for (var pc : toUpdate.getProjectContractors()) {
                pc.setProject(toUpdate);
            }
            
            logger.info("Updated {} contractors for project {}", project.getProjectContractors().size(), id);
        }
        
        Project updatedProject = projectRepository.save(toUpdate);
        logger.info("Project {} updated for tenant: {}", id, tenant);
        
        return updatedProject;
    }
    
    /**
     * Retrieves a project by ID
     */
    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching for project {} for tenant: {}", id, tenant);
        
        return projectRepository.findById(id);
    }
    
    /**
     * Lists all projects with pagination
     */
    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(Pageable pageable) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving all projects for tenant: {}", tenant);
        
        return projectRepository.findAll(pageable);
    }
    
    /**
     * Searches projects by name
     */
    @Transactional(readOnly = true)
    public List<Project> searchProjectsByName(String name) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching projects by name '{}' for tenant: {}", name, tenant);
        
        return projectRepository.searchByName(name);
    }
    
    /**
     * Retrieves projects by owner
     */
    public List<Project> getProjectsByOwner(UUID ownerId) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving projects for owner {} for tenant: {}", ownerId, tenant);
        
        return projectRepository.findAllByOwnerId(ownerId);
    }
    
    /**
     * Retrieves projects by status
     */
    @Transactional(readOnly = true)
    public List<Project> getProjectsByStatus(ProjectStatus status) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving projects with status {} for tenant: {}", status, tenant);
        
        return projectRepository.findByStatus(status);
    }
    
    /**
     * Retrieves active projects
     */
    @Transactional(readOnly = true)
    public List<Project> getActiveProjects() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving active projects for tenant: {}", tenant);
        
        return projectRepository.findActiveProjects();
    }
    
    /**
     * Add contractor to project
     */
    public void addContractorToProject(UUID projectId, UUID contractorId, String role, BigDecimal contractAmount) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Adding contractor {} to project {} for tenant: {}", contractorId, projectId, tenant);
        
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        
        Project projectEntity = project.get();
        
        // Check if contractor already exists in project
        if (projectEntity.hasContractor(contractorId)) {
            throw new IllegalArgumentException("Contractor already assigned to this project");
        }
        
        projectEntity.addContractor(contractorId, role, contractAmount);
        projectRepository.save(projectEntity);
        
        logger.info("Contractor {} added to project {} for tenant: {}", contractorId, projectId, tenant);
    }
    
    /**
     * Remove contractor from project
     */
    public void removeContractorFromProject(UUID projectId, UUID contractorId) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Removing contractor {} from project {} for tenant: {}", contractorId, projectId, tenant);
        
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        
        Project projectEntity = project.get();
        projectEntity.removeContractor(contractorId);
        projectRepository.save(projectEntity);
        
        logger.info("Contractor {} removed from project {} for tenant: {}", contractorId, projectId, tenant);
    }
    
    /**
     * Update AI analysis status
     */
    public void updateAIAnalysis(UUID projectId, AIAnalysisStatus status, BigDecimal complianceScore, String summary) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Updating AI analysis for project {} to status {} for tenant: {}", projectId, status, tenant);
        
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        
        Project projectEntity = project.get();
        projectEntity.getAiAnalysis().markAsAnalyzed(status, complianceScore, summary);
        
        projectRepository.save(projectEntity);
        
        logger.info("AI analysis updated for project {} for tenant: {}", projectId, tenant);
    }
    
    /**
     * Get projects needing insurance validation
     */
    @Transactional(readOnly = true)
    public List<Project> getProjectsNeedingValidation() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving projects needing validation for tenant: {}", tenant);
        
        return projectRepository.findProjectsNeedingAnalysis();
    }
    
    /**
     * Archive a project
     */
    public void archiveProject(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Archiving project {} for tenant: {}", id, tenant);
        
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new IllegalArgumentException("Project not found: " + id);
        }
        
        Project projectEntity = project.get();
        projectEntity.setStatus(ProjectStatus.CANCELLED);
        projectRepository.save(projectEntity);
        
        logger.info("Project {} archived for tenant: {}", id, tenant);
    }
    
    /**
     * Get project statistics for current tenant
     */
    @Transactional(readOnly = true)
    public ProjectStats getProjectStats() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Generating project stats for tenant: {}", tenant);
        
        return new ProjectStats(
            projectRepository.countByStatus(ProjectStatus.PLANNED),
            projectRepository.countByStatus(ProjectStatus.STARTED),
            projectRepository.countByStatus(ProjectStatus.IN_PROGRESS),
            projectRepository.countByStatus(ProjectStatus.COMPLETED),
            projectRepository.countByStatus(ProjectStatus.CANCELLED),
            projectRepository.countByAIStatus(AIAnalysisStatus.PENDING),
            projectRepository.countByAIStatus(AIAnalysisStatus.COMPLIANT),
            projectRepository.countByAIStatus(AIAnalysisStatus.NON_COMPLIANT),
            projectRepository.count()
        );
    }
    
    /**
     * Statistics class
     */
    public static class ProjectStats {
        private final long plannedCount;
        private final long startedCount;
        private final long inProgressCount;
        private final long completedCount;
        private final long cancelledCount;
        private final long pendingAnalysisCount;
        private final long compliantCount;
        private final long nonCompliantCount;
        private final long totalCount;
        
        public ProjectStats(long plannedCount, long startedCount, long inProgressCount, 
                          long completedCount, long cancelledCount,
                          long pendingAnalysisCount, long compliantCount, long nonCompliantCount,
                          long totalCount) {
            this.plannedCount = plannedCount;
            this.startedCount = startedCount;
            this.inProgressCount = inProgressCount;
            this.completedCount = completedCount;
            this.cancelledCount = cancelledCount;
            this.pendingAnalysisCount = pendingAnalysisCount;
            this.compliantCount = compliantCount;
            this.nonCompliantCount = nonCompliantCount;
            this.totalCount = totalCount;
        }
        
        // Getters
        public long getPlannedCount() { return plannedCount; }
        public long getStartedCount() { return startedCount; }
        public long getInProgressCount() { return inProgressCount; }
        public long getCompletedCount() { return completedCount; }
        public long getCancelledCount() { return cancelledCount; }
        public long getPendingAnalysisCount() { return pendingAnalysisCount; }
        public long getCompliantCount() { return compliantCount; }
        public long getNonCompliantCount() { return nonCompliantCount; }
        public long getTotalCount() { return totalCount; }
        
        public long getActiveCount() {
            return startedCount + inProgressCount;
        }
        
        @Override
        public String toString() {
            return String.format("ProjectStats{total=%d, active=%d, completed=%d, compliant=%d, pending=%d}", 
                               totalCount, getActiveCount(), completedCount, compliantCount, pendingAnalysisCount);
        }
    }
}