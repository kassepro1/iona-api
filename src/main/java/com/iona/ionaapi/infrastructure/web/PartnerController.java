package com.iona.ionaapi.infrastructure.web;

import com.iona.ionaapi.application.service.PartnerService;
import com.iona.ionaapi.application.service.dto.PartnerStats;
import com.iona.ionaapi.domain.partner.enums.PartnerType;
import com.iona.ionaapi.domain.partner.Partner;
import com.iona.ionaapi.infrastructure.dto.CreatePartnerRequest;
import com.iona.ionaapi.infrastructure.dto.PagedPartnerResponse;
import com.iona.ionaapi.infrastructure.dto.PartnerResponse;
import com.iona.ionaapi.infrastructure.dto.UpdatePartnerRequest;
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
import java.util.UUID;

/**
 * Contrôleur REST pour la gestion des Partners
 * Démontre l'utilisation de l'API multi-tenant
 * Chaque opération s'exécute dans le contexte du tenant courant
 */
@RestController
@RequestMapping("/api/v1/partners")
public class PartnerController {
    
    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);
    
    private final PartnerService partnerService;
    
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }
    
    /**
     * Crée un nouveau partner
     * 
     * Test:
     * curl -X POST "http://localhost:8080/api/partners" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: courtier-lacroix" \
     *      -d '{"name": "Partner Test", "partnerType": "BROKER", "siret": "12345678901234", "contactEmail": "test@example.com"}'
     */
    @PostMapping
    public ResponseEntity<PartnerResponse> createPartner(@RequestBody CreatePartnerRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Création d'un partner pour le tenant: {}", tenant);
            
            // Convertit la requête en entité
            Partner partner = new Partner();
            partner.setName(request.getName());
            partner.setPartnerType(request.getPartnerType());
            partner.setSiret(request.getSiret());
            partner.setContactEmail(request.getContactEmail());
            partner.setPhone(request.getPhone());
            
            if (request.getAddress() != null) {
                partner.setAddress(request.getAddress());
            }
            
            Partner createdPartner = partnerService.createPartner(partner);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PartnerResponse(true, "Partner créé avec succès", createdPartner, tenant));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création du partner", e);
            return ResponseEntity.badRequest()
                .body(new PartnerResponse(false, "Erreur: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Récupère tous les partners avec pagination
     * 
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/partners?page=0&size=10&sort=name"
     */
    @GetMapping
    public ResponseEntity<PagedPartnerResponse> getAllPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {
        
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Récupération des partners pour le tenant: {} (page={}, size={})", tenant, page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<Partner> partners = partnerService.getAllPartners(pageable);
            
            return ResponseEntity.ok(new PagedPartnerResponse(true, "Partners récupérés", partners, tenant));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des partners", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère un partner par ID
     * 
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/partners/{id}"
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerResponse> getPartnerById(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Récupération du partner {} pour le tenant: {}", id, tenant);
            
            Optional<Partner> partner = partnerService.getPartnerById(id);
            
            if (partner.isPresent()) {
                return ResponseEntity.ok(new PartnerResponse(true, "Partner trouvé", partner.get(), tenant));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du partner: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Met à jour un partner
     * 
     * Test:
     * curl -X PUT "http://localhost:8080/api/partners/{id}" \
     *      -H "Content-Type: application/json" \
     *      -H "X-Tenant-ID: courtier-lacroix" \
     *      -d '{"name": "Partner Updated"}'
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartnerResponse> updatePartner(@PathVariable UUID id, @RequestBody UpdatePartnerRequest request) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Mise à jour du partner {} pour le tenant: {}", id, tenant);
            
            // Convertit la requête en entité
            Partner partnerUpdates = new Partner();
            partnerUpdates.setName(request.getName());
            partnerUpdates.setContactEmail(request.getContactEmail());
            partnerUpdates.setPhone(request.getPhone());
            partnerUpdates.setStatus(request.getStatus());
            partnerUpdates.setAddress(request.getAddress());
            
            Partner updatedPartner = partnerService.updatePartner(id, partnerUpdates);
            
            return ResponseEntity.ok(new PartnerResponse(true, "Partner mis à jour", updatedPartner, tenant));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du partner: {}", id, e);
            return ResponseEntity.badRequest()
                .body(new PartnerResponse(false, "Erreur: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Archive un partner (soft delete)
     * 
     * Test:
     * curl -X PATCH "http://localhost:8080/api/partners/{id}/archive" \
     *      -H "X-Tenant-ID: courtier-lacroix"
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<PartnerResponse> archivePartner(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.info("Archivage du partner {} pour le tenant: {}", id, tenant);
            
            partnerService.archivePartner(id);
            
            return ResponseEntity.ok(new PartnerResponse(true, "Partner archivé", null, tenant));
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'archivage du partner: {}", id, e);
            return ResponseEntity.badRequest()
                .body(new PartnerResponse(false, "Erreur: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Supprime définitivement un partner
     * 
     * Test:
     * curl -X DELETE "http://localhost:8080/api/partners/{id}" \
     *      -H "X-Tenant-ID: courtier-lacroix"
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PartnerResponse> deletePartner(@PathVariable UUID id) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.warn("Suppression du partner {} pour le tenant: {}", id, tenant);
            
            partnerService.deletePartner(id);
            
            return ResponseEntity.ok(new PartnerResponse(true, "Partner supprimé", null, tenant));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du partner: {}", id, e);
            return ResponseEntity.badRequest()
                .body(new PartnerResponse(false, "Erreur: " + e.getMessage(), null, TenantContext.getTenantOrDefault()));
        }
    }
    
    /**
     * Recherche des partners par nom
     * 
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/partners/search?name=test"
     */
    @GetMapping("/search")
    public ResponseEntity<List<Partner>> searchPartners(@RequestParam String name) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Recherche de partners par nom '{}' pour le tenant: {}", name, tenant);
            
            List<Partner> partners = partnerService.searchPartnersByName(name);
            return ResponseEntity.ok(partners);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de partners", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère les partners par type
     * 
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/partners/type/BROKER"
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Partner>> getPartnersByType(@PathVariable PartnerType type) {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Récupération des partners de type {} pour le tenant: {}", type, tenant);
            
            List<Partner> partners = partnerService.getPartnersByType(type);
            return ResponseEntity.ok(partners);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des partners par type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupère les statistiques des partners
     * 
     * Test:
     * curl -H "X-Tenant-ID: courtier-lacroix" "http://localhost:8080/api/partners/stats"
     */
    @GetMapping("/stats")
    public ResponseEntity<PartnerStats> getPartnerStats() {
        try {
            String tenant = TenantContext.getTenantOrDefault();
            logger.debug("Génération des stats partners pour le tenant: {}", tenant);
            
            PartnerStats stats = partnerService.getPartnerStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la génération des stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Classes DTO pour les requêtes/réponses
    // (voir fichier séparé pour les DTOs complets)
}