package com.iona.ionaapi.infrastructure.web;

import com.iona.ionaapi.infrastructure.dto.TenantInfo;
import com.iona.ionaapi.infrastructure.dto.TenantResponse;
import com.iona.ionaapi.infrastructure.tenant.TenantService;
import com.iona.ionaapi.infrastructure.config.DataSourceConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion programmatique des tenants
 * Permet de créer, lister et gérer les tenants dynamiquement
 */
@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    
    private final TenantService tenantService;
    private final DataSourceConfig dataSourceConfig;
    
    public TenantController(TenantService tenantService, DataSourceConfig dataSourceConfig) {
        this.tenantService = tenantService;
        this.dataSourceConfig = dataSourceConfig;
    }
    
    /**
     * Crée un nouveau tenant avec son schema
     */
    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody CreateTenantRequest request) {
        try {
            logger.info("Demande de création du tenant: {}", request.getTenantId());
            
            // Valide la requête
            if (request.getTenantId() == null || request.getTenantId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new TenantResponse(false, "Tenant ID requis", null));
            }
            
            String tenantId = request.getTenantId().trim();
            
            // Vérifie si le tenant existe déjà
            if (tenantService.tenantExists(tenantId)) {
                return ResponseEntity.ok()
                    .body(new TenantResponse(true, "Tenant déjà existant", createTenantInfo(tenantId)));
            }
            
            // Crée le tenant
            tenantService.createTenant(tenantId);
            
            // Ajoute la DataSource (optionnel selon l'implémentation)
            try {
                dataSourceConfig.addTenantDataSource(tenantId);
            } catch (Exception e) {
                logger.warn("Avertissement lors de l'ajout de la DataSource pour {}: {}", tenantId, e.getMessage());
            }
            
            TenantInfo tenantInfo = createTenantInfo(tenantId);
            
            logger.info("Tenant '{}' créé avec succès", tenantId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TenantResponse(true, "Tenant créé avec succès", tenantInfo));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création du tenant: {}", request.getTenantId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new TenantResponse(false, "Erreur lors de la création: " + e.getMessage(), null));
        }
    }
    
    /**
     * Liste tous les tenants
     */
    @GetMapping
    public ResponseEntity<List<TenantService.TenantInfo>> getAllTenants() {
        try {
            List<TenantService.TenantInfo> tenants = tenantService.getAllTenants();
            logger.debug("Récupération de {} tenants", tenants.size());
            return ResponseEntity.ok(tenants);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des tenants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Vérifie si un tenant existe
     */
    @GetMapping("/{tenantId}/exists")
    public ResponseEntity<Map<String, Object>> checkTenantExists(@PathVariable String tenantId) {
        try {
            boolean exists = tenantService.tenantExists(tenantId);
            boolean valid = tenantService.isValidTenantId(tenantId);
            
            return ResponseEntity.ok(Map.of(
                "tenantId", tenantId,
                "exists", exists,
                "valid", valid
            ));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Supprime un tenant (ATTENTION: supprime toutes les données)
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> deleteTenant(@PathVariable String tenantId) {
        try {
            logger.warn("Demande de suppression du tenant: {}", tenantId);
            
            if (!tenantService.tenantExists(tenantId)) {
                return ResponseEntity.notFound().build();
            }
            
            tenantService.deleteTenant(tenantId);
            
            logger.warn("Tenant '{}' supprimé", tenantId);
            return ResponseEntity.ok()
                .body(new TenantResponse(true, "Tenant supprimé avec succès", null));
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new TenantResponse(false, "Erreur lors de la suppression: " + e.getMessage(), null));
        }
    }
    
    /**
     * Obtient les informations d'un tenant spécifique
     */
    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantInfo> getTenantInfo(@PathVariable String tenantId) {
        try {
            if (!tenantService.tenantExists(tenantId)) {
                return ResponseEntity.notFound().build();
            }
            
            TenantInfo info = createTenantInfo(tenantId);
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des infos du tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crée les informations d'un tenant
     */
    private TenantInfo createTenantInfo(String tenantId) {
        return new TenantInfo(tenantId, "tenant_" + tenantId.toLowerCase(), 
                             tenantService.tenantExists(tenantId));
    }
    
    // Classes DTO
    public static class CreateTenantRequest {
        private String tenantId;
        private String description;
        
        // Getters et Setters
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    

    

}