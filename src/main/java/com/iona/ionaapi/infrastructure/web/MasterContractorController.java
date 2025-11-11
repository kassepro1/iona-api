package com.iona.ionaapi.infrastructure.web;

import com.iona.ionaapi.application.service.MasterContractorService;
import com.iona.ionaapi.domain.mastercontractor.MasterContractor;
import com.iona.ionaapi.domain.mastercontractor.Contact;
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

import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for MasterContractor management
 * Demonstrates multi-tenant API usage
 * Each operation executes in the current tenant context
 */
@RestController
@RequestMapping("/api/master-contractors")
public class MasterContractorController {

    private static final Logger logger = LoggerFactory.getLogger(MasterContractorController.class);

    private final MasterContractorService masterContractorService;

    public MasterContractorController(MasterContractorService masterContractorService) {
        this.masterContractorService = masterContractorService;
    }

    /**
     * Creates a new master contractor
     *
     * Test with contacts:
     * curl -X POST "http://localhost:8080/api/master-contractors" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Bouygues Construction",
     *        "type": "PRIVATE",
     *        "siret": "39478149800034",
     *        "address": "3 Avenue du Centre, 75001 Paris",
     *        "contacts": [
     *          {
     *            "firstName": "Jean",
     *            "lastName": "Dupont",
     *            "email": "jean.dupont@bouygues.com",
     *            "phone": "0123456789",
     *            "position": "Project Manager",
     *            "isPrimary": true
     *          }
     *        ]
     *      }'
     *
     * Test without contacts:
     * curl -X POST "http://localhost:8080/api/master-contractors" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{"name": "Vinci Construction", "type": "PRIVATE", "siret": "55208639500011", "address": "1 Cours Ferdinand de Lesseps, 92500 Rueil-Malmaison"}'
     */
    @PostMapping
    public ResponseEntity<MasterContractorResponse> createMasterContractor(@RequestBody CreateMasterContractorRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Creating master contractor for tenant: {}", tenant);

            // Convert request to entity
            MasterContractor masterContractor = new MasterContractor();
            masterContractor.setName(request.getName());
            masterContractor.setType(request.getType());
            masterContractor.setSiret(request.getSiret());
            masterContractor.setAddress(request.getAddress());

            // Add contacts if provided
            if (request.getContacts() != null && !request.getContacts().isEmpty()) {
                logger.info("Adding {} contacts to master contractor", request.getContacts().size());

                for (ContactRequest contactReq : request.getContacts()) {
                    Contact contact = new Contact(
                            contactReq.getFirstName(),
                            contactReq.getLastName(),
                            contactReq.getEmail(),
                            contactReq.getPhone()
                    );
                    contact.setPosition(contactReq.getPosition());
                    contact.setPrimary(contactReq.isPrimary());
                    masterContractor.addContact(contact);

                    logger.debug("Added contact: {} {} (Primary: {})",
                            contactReq.getFirstName(), contactReq.getLastName(), contactReq.isPrimary());
                }
            } else {
                logger.info("No contacts provided for master contractor");
            }

            MasterContractor createdMasterContractor = masterContractorService.createMasterContractor(masterContractor);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MasterContractorResponse(true, "Master contractor created successfully", createdMasterContractor, tenant));

        } catch (Exception e) {
            logger.error("Error creating master contractor", e);
            return ResponseEntity.badRequest()
                    .body(new MasterContractorResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }

    /**
     * Updates an existing master contractor
     *
     * Test with contacts:
     * curl -X PUT "http://localhost:8080/api/master-contractors/{id}" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: vers" \
     *      -d '{
     *        "name": "Bouygues Construction SA",
     *        "address": "1 Avenue Eugène Freyssinet, 78280 Guyancourt",
     *        "contacts": [
     *          {
     *            "firstName": "Marie",
     *            "lastName": "Martin",
     *            "email": "marie.martin@bouygues.com",
     *            "phone": "0123456790",
     *            "position": "Site Manager",
     *            "isPrimary": true
     *          }
     *        ]
     *      }'
     */
    @PutMapping("/{id}")
    public ResponseEntity<MasterContractorResponse> updateMasterContractor(@PathVariable UUID id, @RequestBody UpdateMasterContractorRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Updating master contractor {} for tenant: {}", id, tenant);

            // Convert request to entity
            MasterContractor masterContractorUpdates = new MasterContractor();
            masterContractorUpdates.setName(request.getName());
            masterContractorUpdates.setType(request.getType());
            masterContractorUpdates.setAddress(request.getAddress());
            masterContractorUpdates.setSiret(request.getSiret());

            // Add contacts if provided
            if (request.getContacts() != null && !request.getContacts().isEmpty()) {
                logger.info("Updating {} contacts for master contractor {}", request.getContacts().size(), id);

                for (ContactRequest contactReq : request.getContacts()) {
                    Contact contact = new Contact(
                            contactReq.getFirstName(),
                            contactReq.getLastName(),
                            contactReq.getEmail(),
                            contactReq.getPhone()
                    );
                    contact.setPosition(contactReq.getPosition());
                    contact.setPrimary(contactReq.isPrimary());
                    masterContractorUpdates.addContact(contact);
                }
            }

            MasterContractor updatedMasterContractor = masterContractorService.updateMasterContractor(id, masterContractorUpdates);

            return ResponseEntity.ok(new MasterContractorResponse(true, "Master contractor updated successfully", updatedMasterContractor, tenant));

        } catch (Exception e) {
            logger.error("Error updating master contractor: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new MasterContractorResponse(false, "Error: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }

    /**
     * Retrieves all master contractors with pagination
     *
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/master-contractors?page=0&size=10&sort=name"
     */
    @GetMapping
    public ResponseEntity<PagedMasterContractorResponse> getAllMasterContractors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {

        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving master contractors for tenant: {} (page={}, size={})", tenant, page, size);

            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<MasterContractor> masterContractors = masterContractorService.getAllMasterContractors(pageable);

            return ResponseEntity.ok(new PagedMasterContractorResponse(true, "Master contractors retrieved", masterContractors, tenant));

        } catch (Exception e) {
            logger.error("Error retrieving master contractors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a master contractor by ID
     *
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/master-contractors/{id}"
     */
    @GetMapping("/{id}")
    public ResponseEntity<MasterContractorResponse> getMasterContractorById(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Retrieving master contractor {} for tenant: {}", id, tenant);

            Optional<MasterContractor> masterContractor = masterContractorService.getMasterContractorById(id);

            if (masterContractor.isPresent()) {
                return ResponseEntity.ok(new MasterContractorResponse(true, "Master contractor found", masterContractor.get(), tenant));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Error retrieving master contractor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}