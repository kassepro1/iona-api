package com.iona.ionaapi.infrastructure.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contexte de tenant utilisant ThreadLocal pour stocker l'identifiant du tenant actuel
 * Approche simple et efficace pour la gestion multi-tenant
 */
public class TenantContext {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    /**
     * Définit le tenant pour le thread actuel
     */
    public static void setTenant(String tenant) {
        if (tenant != null && !tenant.trim().isEmpty()) {
            CURRENT_TENANT.set(tenant.trim());
            logger.debug("Tenant défini: {}", tenant);
        } else {
            logger.warn("Tentative de définition d'un tenant null ou vide");
        }
    }
    
    /**
     * Récupère le tenant du thread actuel
     */
    public static String getTenant() {
        String tenant = CURRENT_TENANT.get();
        logger.debug("Tenant récupéré: {}", tenant);
        return tenant;
    }
    
    /**
     * Supprime le tenant du thread actuel
     */
    public static void clear() {
        String tenant = CURRENT_TENANT.get();
        if (tenant != null) {
            logger.debug("Suppression du tenant: {}", tenant);
            CURRENT_TENANT.remove();
        }
    }
    
    /**
     * Vérifie si un tenant est défini
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
    
    /**
     * Récupère le tenant ou retourne le tenant par défaut
     */
    public static String getTenantOrDefault() {
        String tenant = getTenant();
        return tenant != null ? tenant : "default";
    }
}