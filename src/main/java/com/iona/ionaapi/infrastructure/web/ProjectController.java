package com.iona.ionaapi.infrastructure.web;

import com.iona.ionaapi.application.service.ProjectService;
import com.iona.ionaapi.domain.project.Project;
import com.iona.ionaapi.domain.project.ProjectContractor;
import com.iona.ionaapi.domain.project.enums.ProjectStatus;
import com.iona.ionaapi.infrastructure.dto.projet.AIAnalysisUpdateRequest;
import com.iona.ionaapi.infrastructure.dto.projet.CreateProjectRequest;
import com.iona.ionaapi.infrastructure.dto.projet.ProjectContractorRequest;
import com.iona.ionaapi.infrastructure.dto.projet.UpdateProjectRequest;
import com.iona.ionaapi.infrastructure.dto.projet.projet.PagedProjectResponse;
import com.iona.ionaapi.infrastructure.dto.projet.projet.ProjectResponse;
import com.iona.ionaapi.infrastructure.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Project management
 * Central controller linking master contractors, contractors, and AI analysis
 * Each operation executes in the current tenant context
 */
@RestController
@RequestMapping("/api/v1/projects")
@CrossOrigin("http://localhost:4200")
public class ProjectController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    /**
     * Creates a new project
     * 
     * Test with contractors:
     * curl -X POST "http://localhost:8080/api/projects" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Résidence Les Jardins",
     *        "address": "123 Avenue de la République, 75011 Paris",
     *        "cost": 2500000.00,
     *        "openingDate": "2025-03-01",
     *        "mission": "Construction de 24 logements sociaux avec espaces verts",
     *        "ownerId": "master-contractor-uuid-here",
     *        "contractors": [
     *          {
     *            "contractorId": "contractor-uuid-1",
     *            "role": "Électricité générale",
     *            "name": "Nord Elec",
     *            "contractAmount": 150000.00,
     *            "startDate": "2025-03-15"
     *          },
     *          {
     *            "contractorId": "contractor-uuid-2", 
     *            "role": "Plomberie et chauffage",
     *            "name": "Nord Plomb",
     *            "contractAmount": 120000.00,
     *            "startDate": "2025-04-01"
     *          }
     *        ]
     *      }'
     * 
     * Test simple without contractors:
     * curl -X POST "http://localhost:8080/api/projects" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Bureaux Centre-Ville",
     *        "address": "45 Rue du Commerce, 75015 Paris", 
     *        "cost": 800000.00,
     *        "ownerId": "master-contractor-uuid-here",
     *        "mission": "Rénovation bureaux de 500m²"
     *      }'
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Creating project for tenant: {}", tenant);
            
            // Convert request to entity
            Project project = new Project();
            project.setName(request.getName());
            project.setAddress(request.getAddress());
            project.setCost(request.getCost());
            project.setOpeningDate(request.getOpeningDate());
            project.setMission(request.getMission());
            project.setOwnerId(request.getOwnerId());
            
            if (request.getStatus() != null) {
                project.setStatus(request.getStatus());
            }
            
            // Add contractors if provided
            if (request.getContractors() != null && !request.getContractors().isEmpty()) {
                logger.info("Adding {} contractors to project", request.getContractors().size());
                
                for (ProjectContractorRequest contractorReq : request.getContractors()) {
                    ProjectContractor projectContractor = new ProjectContractor();
                    projectContractor.setProject(project);
                    //fixme verifier contractorReq.getContractorId()

                    projectContractor.setContractorId(contractorReq.getContractorId());
                    projectContractor.setRole(contractorReq.getRole());
                    projectContractor.setName(contractorReq.getName());
                    projectContractor.setContractAmount(contractorReq.getContractAmount());
                    projectContractor.setStartDate(contractorReq.getStartDate());
                    projectContractor.setEndDate(contractorReq.getEndDate());
                    
                    project.getProjectContractors().add(projectContractor);
                    
                    logger.debug("Added contractor: {} for role: {}", 
                        contractorReq.getContractorId(), contractorReq.getRole());
                }
            } else {
                logger.info("No contractors provided for project");
            }
            
            Project createdProject = projectService.createProject(project);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProjectResponse(true, "Project created successfully", createdProject, tenant));
            
        } catch (Exception e) {
            logger.error("Error creating project", e);
            return ResponseEntity.badRequest()
                .body(new ProjectResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Updates an existing project
     * 
     * Test with contractor updates:
     * curl -X PUT "http://localhost:8080/api/projects/{id}" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Résidence Les Jardins - Phase 1",
     *        "cost": 2700000.00,
     *        "status": "STARTED", 
     *        "contractors": [
     *          {
     *            "contractorId": "contractor-uuid-1",
     *            "role": "Électricité générale + Domotique",
     *            "name": "Sim Électricité",
     *            "contractAmount": 180000.00,
     *            "startDate": "2025-03-15",
     *            "endDate": "2025-06-15"
     *          }
     *        ]
     *      }'
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id, @RequestBody UpdateProjectRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Updating project {} for tenant: {}", id, tenant);
            
            // Convert request to entity
            Project projectUpdates = new Project();
            projectUpdates.setName(request.getName());
            projectUpdates.setAddress(request.getAddress());
            projectUpdates.setCost(request.getCost());
            projectUpdates.setOpeningDate(request.getOpeningDate());
            projectUpdates.setMission(request.getMission());
            projectUpdates.setStatus(request.getStatus());
            projectUpdates.setOwnerId(request.getOwnerId());
            
            // Update contractors if provided
            if (request.getContractors() != null && !request.getContractors().isEmpty()) {
                logger.info("Updating {} contractors for project {}", request.getContractors().size(), id);
                
                for (ProjectContractorRequest contractorReq : request.getContractors()) {
                    ProjectContractor projectContractor = new ProjectContractor();
                    //fixme verifier contractorReq.getContractorId()
                    projectContractor.setContractorId(contractorReq.getContractorId());
                    projectContractor.setRole(contractorReq.getRole());
                    projectContractor.setName(contractorReq.getName());
                    projectContractor.setContractAmount(contractorReq.getContractAmount());
                    projectContractor.setStartDate(contractorReq.getStartDate());
                    projectContractor.setEndDate(contractorReq.getEndDate());
                    
                    projectUpdates.getProjectContractors().add(projectContractor);
                }
            }
            
            Project updatedProject = projectService.updateProject(id, projectUpdates);
            
            return ResponseEntity.ok(new ProjectResponse(true, "Project updated successfully", updatedProject, tenant));
            
        } catch (Exception e) {
            logger.error("Error updating project: {}", id, e);
            return ResponseEntity.badRequest()
                .body(new ProjectResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Retrieves all projects with pagination
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects?page=0&size=10&sort=name"
     */
    @GetMapping
    public ResponseEntity<PagedProjectResponse> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {
        
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving projects for tenant: {} (page={}, size={})", tenant, page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<Project> projects = projectService.getAllProjects(pageable);
            
            return ResponseEntity.ok(new PagedProjectResponse(true, "Projects retrieved", projects, tenant));
            
        } catch (Exception e) {
            logger.error("Error retrieving projects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a project by ID
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/{id}"
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving project {} for tenant: {}", id, tenant);
            
            Optional<Project> project = projectService.getProjectById(id);
            
            if (project.isPresent()) {
                return ResponseEntity.ok(new ProjectResponse(true, "Project found", project.get(), tenant));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving project: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Additional useful endpoints
     */
    
    /**
     * Search projects by name
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/search?name=jardins"
     */
    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam String name) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Searching projects by name '{}' for tenant: {}", name, tenant);
            
            List<Project> projects = projectService.searchProjectsByName(name);
            return ResponseEntity.ok(projects);
            
        } catch (Exception e) {
            logger.error("Error searching projects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get projects by status
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/status/IN_PROGRESS"
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving projects with status {} for tenant: {}", status, tenant);
            
            List<Project> projects = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(projects);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get projects by owner (master contractor)
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/owner/{ownerId}"
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Project>> getProjectsByOwner(@PathVariable UUID ownerId) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving projects for owner {} for tenant: {}", ownerId, tenant);
            
            List<Project> projects = projectService.getProjectsByOwner(ownerId);
            return ResponseEntity.ok(projects);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects by owner", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get active projects
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/active"
     */
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving active projects for tenant: {}", tenant);
            
            List<Project> projects = projectService.getActiveProjects();
            return ResponseEntity.ok(projects);
            
        } catch (Exception e) {
            logger.error("Error retrieving active projects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Add contractor to project
     * 
     * Test:
     * curl -X POST "http://localhost:8080/api/projects/{projectId}/contractors" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "contractorId": "contractor-uuid-3",
     *        "role": "Peinture et finitions", 
     *        "contractAmount": 80000.00,
     *        "startDate": "2025-05-01"
     *      }'
     */
    @PostMapping("/{projectId}/contractors")
    public ResponseEntity<ProjectResponse> addContractorToProject(
            @PathVariable UUID projectId, 
            @RequestBody ProjectContractorRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Adding contractor to project {} for tenant: {}", projectId, tenant);
            
            projectService.addContractorToProject(
                projectId, 
                request.getContractorId(),
                request.getRole(),
                request.getContractAmount()
            );
            
            Optional<Project> updatedProject = projectService.getProjectById(projectId);
            
            return ResponseEntity.ok(new ProjectResponse(true, "Contractor added to project", 
                updatedProject.orElse(null), tenant));
            
        } catch (Exception e) {
            logger.error("Error adding contractor to project: {}", projectId, e);
            return ResponseEntity.badRequest()
                .body(new ProjectResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Update AI analysis status
     * 
     * Test:
     * curl -X PUT "http://localhost:8080/api/projects/{projectId}/ai-analysis" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "status": "COMPLIANT",
     *        "complianceScore": 95.5,
     *        "summary": "Toutes les attestations d assurance sont conformes aux exigences légales"
     *      }'
     */
    @PutMapping("/{projectId}/ai-analysis")
    public ResponseEntity<ProjectResponse> updateAIAnalysis(
            @PathVariable UUID projectId, 
            @RequestBody AIAnalysisUpdateRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Updating AI analysis for project {} for tenant: {}", projectId, tenant);
            
            projectService.updateAIAnalysis(
                projectId,
                request.getStatus(),
                request.getComplianceScore(),
                request.getSummary()
            );
            
            Optional<Project> updatedProject = projectService.getProjectById(projectId);
            
            return ResponseEntity.ok(new ProjectResponse(true, "AI analysis updated", 
                updatedProject.orElse(null), tenant));
            
        } catch (Exception e) {
            logger.error("Error updating AI analysis for project: {}", projectId, e);
            return ResponseEntity.badRequest()
                .body(new ProjectResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Get project statistics
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/projects/stats"
     */
    @GetMapping("/stats")
    public ResponseEntity<ProjectService.ProjectStats> getProjectStats() {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Generating project stats for tenant: {}", tenant);
            
            ProjectService.ProjectStats stats = projectService.getProjectStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error generating project stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}