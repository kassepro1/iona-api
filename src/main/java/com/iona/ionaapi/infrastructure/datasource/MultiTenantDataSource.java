package com.iona.ionaapi.infrastructure.datasource;

import com.iona.ionaapi.infrastructure.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * DataSource multi-tenant qui route dynamiquement vers les schemas PostgreSQL
 * selon le tenant courant défini dans TenantContext
 */
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantDataSource.class);

    /**
     * Détermine quel schema utiliser basé sur le tenant actuel
     * Cette méthode est appelée à chaque accès à la base de données
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getTenant();
        String schema = resolveSchemaName(tenant);

        logger.debug("Routing vers le schema: {} pour le tenant: {}", schema, tenant);
        return schema;
    }

    /**
     * Résout le nom du schema PostgreSQL basé sur l'identifiant du tenant
     * Format: tenant_{tenantId} ou "public" par défaut
     */
    private String resolveSchemaName(String tenant) {
        if (tenant == null || tenant.trim().isEmpty()) {
            logger.debug("Aucun tenant défini, utilisation du schema public");
            return "public";
        }

        // Nettoie et normalise le nom du tenant
        String cleanTenant = tenant.toLowerCase()
                .replaceAll("[^a-z0-9_-]", "_")  // Remplace caractères invalides
                .replaceAll("_{2,}", "_");        // Supprime les underscores multiples

        return "tenant_" + cleanTenant;
    }
}