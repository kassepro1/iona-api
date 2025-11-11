package com.iona.ionaapi.infrastructure.web;

import com.iona.ionaapi.application.service.ContractorService;
import com.iona.ionaapi.domain.contractor.Contractor;
import com.iona.ionaapi.domain.contractor.ContractorContact;
import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.infrastructure.dto.*;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * REST Controller for Contractor management
 * Demonstrates multi-tenant API usage for construction companies
 * Each operation executes in the current tenant context
 */
@RestController
@RequestMapping("/api/contractors")
public class ContractorController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractorController.class);
    
    private final ContractorService contractorService;
    
    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }
    
    /**
     * Creates a new contractor
     * 
     * Test with specialties and contacts:
     * curl -X POST "http://localhost:8080/api/contractors" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Électricité Martin SARL", 
     *        "type": "SPECIALIST", 
     *        "siret": "12345678901234", 
     *        "address": "15 Rue des Artisans, 75011 Paris",
     *        "specialties": ["ELECTRICITY", "SECURITY"],
     *        "contacts": [
     *          {
     *            "firstName": "Paul", 
     *            "lastName": "Martin", 
     *            "email": "p.martin@elec-martin.fr", 
     *            "phone": "0143567890", 
     *            "position": "Gérant", 
     *            "isPrimary": true
     *          }
     *        ]
     *      }'
     * 
     * Test simple without contacts:
     * curl -X POST "http://localhost:8080/api/contractors" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Plomberie Durand", 
     *        "type": "SUBCONTRACTOR", 
     *        "siret": "98765432109876", 
     *        "address": "8 Avenue de la République, 92100 Boulogne",
     *        "specialties": ["PLUMBING"]
     *      }'
     */
    @PostMapping
    public ResponseEntity<ContractorResponse> createContractor(@RequestBody CreateContractorRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Creating contractor for tenant: {}", tenant);
            
            // Convert request to entity
            Contractor contractor = new Contractor();
            contractor.setName(request.getName());
            contractor.setType(request.getType());
            contractor.setSiret(request.getSiret());
            contractor.setAddress(request.getAddress());
            
            // Add specialties
            if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
                contractor.getSpecialties().addAll(request.getSpecialties());
                logger.info("Adding {} specialties to contractor", request.getSpecialties().size());
            }
            
            // Add contacts if provided
            if (request.getContacts() != null && !request.getContacts().isEmpty()) {
                logger.info("Adding {} contacts to contractor", request.getContacts().size());
                
                for (ContractorContactRequest contactReq : request.getContacts()) {
                    ContractorContact contact = new ContractorContact(
                        contactReq.getFirstName(),
                        contactReq.getLastName(),
                        contactReq.getEmail(),
                        contactReq.getPhone()
                    );
                    contact.setPosition(contactReq.getPosition());
                    contact.setPrimary(contactReq.isPrimary());
                    contractor.addContact(contact);
                    
                    logger.debug("Added contact: {} {} (Primary: {})", 
                        contactReq.getFirstName(), contactReq.getLastName(), contactReq.isPrimary());
                }
            } else {
                logger.info("No contacts provided for contractor");
            }
            
            Contractor createdContractor = contractorService.createContractor(contractor);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ContractorResponse(true, "Contractor created successfully", createdContractor, tenant));
            
        } catch (Exception e) {
            logger.error("Error creating contractor", e);
            return ResponseEntity.badRequest()
                .body(new ContractorResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Updates an existing contractor
     * 
     * Test with specialty and contact updates:
     * curl -X PUT "http://localhost:8080/api/contractors/{id}" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Électricité Martin & Fils SARL", 
     *        "address": "20 Rue des Artisans, 75011 Paris",
     *        "specialties": ["ELECTRICITY", "SECURITY", "HVAC"],
     *        "contacts": [
     *          {
     *            "firstName": "Sophie", 
     *            "lastName": "Martin", 
     *            "email": "s.martin@elec-martin.fr", 
     *            "phone": "0143567891", 
     *            "position": "Responsable technique", 
     *            "isPrimary": true
     *          }
     *        ]
     *      }'
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContractorResponse> updateContractor(@PathVariable UUID id, @RequestBody UpdateContractorRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Updating contractor {} for tenant: {}", id, tenant);
            
            // Convert request to entity
            Contractor contractorUpdates = new Contractor();
            contractorUpdates.setName(request.getName());
            contractorUpdates.setType(request.getType());
            contractorUpdates.setAddress(request.getAddress());
            contractorUpdates.setSiret(request.getSiret());
            
            // Add specialties if provided
            if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
                contractorUpdates.getSpecialties().addAll(request.getSpecialties());
                logger.info("Updating {} specialties for contractor {}", request.getSpecialties().size(), id);
            }
            
            // Add contacts if provided
            if (request.getContacts() != null && !request.getContacts().isEmpty()) {
                logger.info("Updating {} contacts for contractor {}", request.getContacts().size(), id);
                
                for (ContractorContactRequest contactReq : request.getContacts()) {
                    ContractorContact contact = new ContractorContact(
                        contactReq.getFirstName(),
                        contactReq.getLastName(),
                        contactReq.getEmail(),
                        contactReq.getPhone()
                    );
                    contact.setPosition(contactReq.getPosition());
                    contact.setPrimary(contactReq.isPrimary());
                    contractorUpdates.addContact(contact);
                }
            }
            
            Contractor updatedContractor = contractorService.updateContractor(id, contractorUpdates);
            
            return ResponseEntity.ok(new ContractorResponse(true, "Contractor updated successfully", updatedContractor, tenant));
            
        } catch (Exception e) {
            logger.error("Error updating contractor: {}", id, e);
            return ResponseEntity.badRequest()
                .body(new ContractorResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Retrieves all contractors with pagination
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/contractors?page=0&size=10&sort=name"
     */
    @GetMapping
    public ResponseEntity<PagedContractorResponse> getAllContractors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {
        
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving contractors for tenant: {} (page={}, size={})", tenant, page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<Contractor> contractors = contractorService.getAllContractors(pageable);
            
            return ResponseEntity.ok(new PagedContractorResponse(true, "Contractors retrieved", contractors, tenant));
            
        } catch (Exception e) {
            logger.error("Error retrieving contractors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a contractor by ID
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/contractors/{id}"
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractorResponse> getContractorById(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving contractor {} for tenant: {}", id, tenant);
            
            Optional<Contractor> contractor = contractorService.getContractorById(id);
            
            if (contractor.isPresent()) {
                return ResponseEntity.ok(new ContractorResponse(true, "Contractor found", contractor.get(), tenant));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving contractor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Additional endpoints for specialties and search
     */
    
    /**
     * Search contractors by specialty
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/contractors/specialty/ELECTRICITY"
     */
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<Contractor>> getContractorsBySpecialty(@PathVariable ContractorSpecialty specialty) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving contractors with specialty {} for tenant: {}", specialty, tenant);
            
            List<Contractor> contractors = contractorService.getContractorsBySpecialty(specialty);
            return ResponseEntity.ok(contractors);
            
        } catch (Exception e) {
            logger.error("Error retrieving contractors by specialty", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search contractors by name
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/contractors/search?name=martin"
     */
    @GetMapping("/search")
    public ResponseEntity<List<Contractor>> searchContractors(@RequestParam String name) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Searching contractors by name '{}' for tenant: {}", name, tenant);
            
            List<Contractor> contractors = contractorService.searchContractorsByName(name);
            return ResponseEntity.ok(contractors);
            
        } catch (Exception e) {
            logger.error("Error searching contractors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get contractor statistics
     * 
     * Test:
     * curl -H "X-Tenant-ID: vers" "http://localhost:8080/api/contractors/stats"
     */
    @GetMapping("/stats")
    public ResponseEntity<ContractorService.ContractorStats> getContractorStats() {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Generating contractor stats for tenant: {}", tenant);
            
            ContractorService.ContractorStats stats = contractorService.getContractorStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error generating contractor stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}