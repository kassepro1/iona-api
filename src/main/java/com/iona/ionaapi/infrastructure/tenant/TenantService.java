package com.iona.ionaapi.infrastructure.tenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour la gestion dynamique des tenants
 * Permet la création, validation et gestion des schemas tenant
 */
@Service
public class TenantService {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    
    // Cache des tenants enregistrés pour éviter les vérifications répétées
    private final Map<String, Boolean> tenantsCache = new ConcurrentHashMap<>();
    
    public TenantService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }
    
    /**
     * Enregistre un nouveau tenant et crée son schema
     */
    @Transactional
    public void createTenant(String tenantId) {
        if (!isValidTenantId(tenantId)) {
            throw new IllegalArgumentException("Tenant ID invalide: " + tenantId);
        }
        
        String schemaName = resolveSchemaName(tenantId);
        
        if (schemaExists(schemaName)) {
            logger.warn("Le schema '{}' existe déjà pour le tenant '{}'", schemaName, tenantId);
            tenantsCache.put(tenantId, true);
            return;
        }
        
        try {
            // Crée le schema
            createSchema(schemaName);
            
            // Met en cache le tenant
            tenantsCache.put(tenantId, true);
            
            logger.info("Tenant '{}' créé avec succès (schema: '{}')", tenantId, schemaName);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création du tenant '{}'", tenantId, e);
            throw new RuntimeException("Impossible de créer le tenant: " + tenantId, e);
        }
    }
    
    /**
     * Vérifie si un tenant existe
     */
    public boolean tenantExists(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }
        
        // Vérifie le cache d'abord
        Boolean cached = tenantsCache.get(tenantId);
        if (cached != null) {
            return cached;
        }
        
        // Vérifie dans la base de données
        String schemaName = resolveSchemaName(tenantId);
        boolean exists = schemaExists(schemaName);
        
        // Met en cache le résultat
        tenantsCache.put(tenantId, exists);
        
        return exists;
    }
    
    /**
     * Valide le format d'un tenant ID
     */
    public boolean isValidTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }
        
        String cleanTenant = tenantId.trim();
        
        // Format: lettres, chiffres, tirets et underscores, longueur 3-50
        return cleanTenant.matches("^[a-zA-Z0-9_-]{3,50}$");
    }
    
    /**
     * Liste tous les schemas tenant disponibles
     */
    public List<TenantInfo> getAllTenants() {
        try {
            String query = """
                SELECT 
                    schema_name,
                    CASE 
                        WHEN schema_name = 'tenant_default' THEN 'default'
                        ELSE REGEXP_REPLACE(schema_name, '^tenant_', '')
                    END as tenant_id,
                    (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = schema_name) as table_count
                FROM information_schema.schemata 
                WHERE schema_name LIKE 'tenant_%'
                ORDER BY schema_name
                """;
            
            return jdbcTemplate.query(query, (rs, rowNum) -> {
                TenantInfo info = new TenantInfo();
                info.setTenantId(rs.getString("tenant_id"));
                info.setSchemaName(rs.getString("schema_name"));
                info.setTableCount(rs.getInt("table_count"));
                return info;
            });
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des tenants", e);
            return List.of();
        }
    }
    
    /**
     * Supprime un tenant (ATTENTION: supprime toutes les données)
     */
    @Transactional
    public void deleteTenant(String tenantId) {
        if (!isValidTenantId(tenantId)) {
            throw new IllegalArgumentException("Tenant ID invalide: " + tenantId);
        }
        
        String schemaName = resolveSchemaName(tenantId);
        
        try {
            if (schemaExists(schemaName)) {
                String dropSQL = "DROP SCHEMA " + schemaName + " CASCADE";
                jdbcTemplate.execute(dropSQL);
                
                // Supprime du cache
                tenantsCache.remove(tenantId);
                
                logger.warn("Tenant '{}' supprimé (schema: '{}')", tenantId, schemaName);
            } else {
                logger.warn("Tentative de suppression d'un tenant inexistant: '{}'", tenantId);
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du tenant '{}'", tenantId, e);
            throw new RuntimeException("Impossible de supprimer le tenant: " + tenantId, e);
        }
    }
    
    /**
     * Crée un schema PostgreSQL
     */
    private void createSchema(String schemaName) {
        String createSQL = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        jdbcTemplate.execute(createSQL);
        logger.debug("Schema '{}' créé", schemaName);
    }
    
    /**
     * Vérifie si un schema existe
     */
    private boolean schemaExists(String schemaName) {
        try {
            String query = "SELECT 1 FROM information_schema.schemata WHERE schema_name = ?";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, schemaName);
            return !result.isEmpty();
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du schema '{}'", schemaName, e);
            return false;
        }
    }
    
    /**
     * Résout le nom du schema basé sur le tenant ID
     */
    private String resolveSchemaName(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return "public";
        }
        
        String cleanTenant = tenantId.toLowerCase()
                .replaceAll("[^a-z0-9_-]", "_")
                .replaceAll("_{2,}", "_");
        
        return "tenant_" + cleanTenant;
    }
    
    /**
     * Informations sur un tenant
     */
    public static class TenantInfo {
        private String tenantId;
        private String schemaName;
        private int tableCount;
        
        // Getters et Setters
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }
        
        public int getTableCount() { return tableCount; }
        public void setTableCount(int tableCount) { this.tableCount = tableCount; }
        
        @Override
        public String toString() {
            return String.format("TenantInfo{tenantId='%s', schemaName='%s', tableCount=%d}", 
                    tenantId, schemaName, tableCount);
        }
    }
}