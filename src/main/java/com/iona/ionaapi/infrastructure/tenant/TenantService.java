package com.iona.ionaapi.infrastructure.tenant;

import org.springframework.beans.factory.annotation.Autowired;
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
     * Enregistre un nouveau tenant et crée son schema avec les tables
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

            // Crée les tables dans le nouveau schema
            createTablesInSchema(schemaName);

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
     * Crée toutes les tables nécessaires dans le nouveau schema
     */
    private void createTablesInSchema(String schemaName) {
        try {
            logger.info("Création des tables dans le schema '{}'", schemaName);

            // S'assurer que les extensions UUID sont disponibles
            enableUuidExtensions();

            // Table partners
            String createPartnersTable = String.format("""
                CREATE TABLE IF NOT EXISTS %s.partners (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    name VARCHAR(255) NOT NULL,
                    partner_type VARCHAR(50) NOT NULL,
                    siret VARCHAR(50) NOT NULL,
                    contact_email VARCHAR(255) NOT NULL,
                    phone VARCHAR(50),
                    street VARCHAR(255),
                    postal_code VARCHAR(20),
                    city VARCHAR(100),
                    country VARCHAR(100),
                    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    UNIQUE(siret)
                )
                """, schemaName);
            jdbcTemplate.execute(createPartnersTable);

            // Table master_contractors
            String createMasterContractorsTable = String.format("""
                CREATE TABLE IF NOT EXISTS %s.master_contractors (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    siret VARCHAR(50) NOT NULL,
                    address TEXT NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    UNIQUE(siret)
                )
                """, schemaName);
            jdbcTemplate.execute(createMasterContractorsTable);

            // Table contacts
            String createContactsTable = String.format("""
                CREATE TABLE IF NOT EXISTS %s.contacts (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    phone VARCHAR(50) NOT NULL,
                    position VARCHAR(100),
                    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                    master_contractor_id UUID,
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    FOREIGN KEY (master_contractor_id) REFERENCES %s.master_contractors(id) ON DELETE CASCADE
                )
                """, schemaName, schemaName);
            jdbcTemplate.execute(createContactsTable);

            // Table projects (pour le futur)
            String createProjectsTable = String.format("""
                CREATE TABLE IF NOT EXISTS %s.projects (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    name VARCHAR(255) NOT NULL,
                    address TEXT NOT NULL,
                    cost DECIMAL(15,2),
                    opening_date DATE,
                    owner_id UUID,
                    mission TEXT,
                    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
                    ai_global_status VARCHAR(50) DEFAULT 'PENDING',
                    ai_progress_percent INTEGER DEFAULT 0,
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    FOREIGN KEY (owner_id) REFERENCES %s.master_contractors(id)
                )
                """, schemaName, schemaName);
            jdbcTemplate.execute(createProjectsTable);

            logger.info("Tables créées avec succès dans le schema '{}'", schemaName);

        } catch (Exception e) {
            logger.error("Erreur lors de la création des tables dans le schema '{}'", schemaName, e);
            throw new RuntimeException("Impossible de créer les tables dans le schema: " + schemaName, e);
        }
    }

    /**
     * Active les extensions UUID nécessaires pour PostgreSQL
     */
    private void enableUuidExtensions() {
        try {
            // Tentative d'activation de l'extension pgcrypto (PostgreSQL moderne)
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto");
            logger.debug("Extension pgcrypto activée");
        } catch (Exception e1) {
            logger.debug("Tentative pgcrypto échouée: {}", e1.getMessage());
            try {
                // Tentative d'activation de l'extension uuid-ossp (PostgreSQL classique)
                jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"");
                logger.debug("Extension uuid-ossp activée");
            } catch (Exception e2) {
                logger.warn("Impossible d'activer les extensions UUID: pgcrypto={}, uuid-ossp={}",
                        e1.getMessage(), e2.getMessage());
                logger.info("Utilisation de UUID générés par l'application");
            }
        }
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

        // Si le tenant commence déjà par "tenant_", on ne l'ajoute pas
        if (cleanTenant.startsWith("tenant_")) {
            return cleanTenant;
        }

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