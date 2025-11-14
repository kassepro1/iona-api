package com.iona.ionaapi.infrastructure.filter;

import com.iona.ionaapi.infrastructure.tenant.TenantContext;
import com.iona.ionaapi.infrastructure.tenant.TenantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre HTTP pour intercepter les requêtes et configurer le contexte tenant
 * Lit le header "X-Tenant-ID" et configure le TenantContext accordingly
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TenantFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    public static final String TENANT_HEADER_NAME = "x-tenant-id";
    
    private final TenantService tenantService;
    
    public TenantFilter(TenantService tenantService) {
        this.tenantService = tenantService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.debug("Traitement de la requête: {} {}", request.getMethod(), requestURI);
        
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Tenant-ID,x-tenant-id,X-Tenant-Id,Authorization");
                return;
            }
//            if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
//                response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
//                response.setHeader("Access-Control-Allow-Credentials", "true");
//                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//                response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Tenant-ID,x-tenant-id,X-Tenant-Id");
//                return;
//            }

            Map<String, Object> debugInfo = new HashMap<>();

            // Tenant actuel du contexte

            // Tous les headers reçus
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
//            debugInfo.put("allHeaders", headers);
            log.info("tenantHeaders {}",headers);

            // Tests spécifiques des headers tenant
            Map<String, String> tenantHeaders = new HashMap<>();
            String[] tenantHeaderVariants = {
                    "X-Tenant-ID",
                    "x-tenant-id",
                    "X-TENANT-ID",
                    "X-Tenant-Id",
                    "X-tenant-id"
            };

            for (String headerName : tenantHeaderVariants) {
                String headerValue = request.getHeader(headerName);
                tenantHeaders.put(headerName, headerValue);
            }
//            debugInfo.put("tenantHeaders", tenantHeaders);
            log.info("tenantHeaders {}",tenantHeaders);
            // Extrait le tenant depuis la requête
//            String tenantId = extractTenantFromRequest(request);
            String tenantId = tenantHeaders.get(TENANT_HEADER_NAME).toString();

            if (shouldValidateTenant(requestURI)) {
                if (tenantId == null || tenantId.trim().isEmpty()) {
                    handleMissingTenant(response, requestURI);
                    return;
                }
                
                if (!tenantService.isValidTenantId(tenantId)) {
                    handleInvalidTenant(response, tenantId, requestURI);
                    return;
                }
                
                // Crée automatiquement le tenant s'il n'existe pas (pour le développement)
                if (!tenantService.tenantExists(tenantId)) {
                    logger.info("Création automatique du tenant: {}", tenantId);
                    tenantService.createTenant(tenantId);
                }
            }
            
            // Définit le contexte tenant
            if (tenantId != null) {
                TenantContext.setTenant(tenantId);
                logger.debug("Contexte tenant configuré: {}", tenantId);
            }

            // Continue le traitement
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Erreur dans le filtre tenant pour la requête: {} {}", 
                    request.getMethod(), requestURI, e);
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Erreur interne du serveur\"}");
            
        } finally {
            // Nettoie toujours le contexte
            TenantContext.clear();
            logger.debug("Contexte tenant nettoyé pour: {} {}", request.getMethod(), requestURI);
        }
    }
    
    /**
     * Extrait l'identifiant du tenant depuis la requête
     */
    private String extractTenantFromRequest(HttpServletRequest request) {

        String tenantId = request.getHeader(TENANT_HEADER_NAME);

        if (tenantId != null && !tenantId.trim().isEmpty()) {
            return tenantId.trim();
        }

        // Priorité 2: Paramètre de requête (pour les tests)
        tenantId = request.getParameter("tenantId");
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            logger.debug("Tenant trouvé dans les paramètres: {}", tenantId);
            return tenantId.trim();
        }

        return null;
    }
    
    /**
     * Détermine si la validation du tenant est nécessaire pour cette URI
     */
    private boolean shouldValidateTenant(String requestURI) {
        // Exclut les endpoints publics de la validation tenant
        return !requestURI.startsWith("/api/public") &&
               !requestURI.startsWith("/api/health") &&
               !requestURI.startsWith("/api/actuator") &&
               !requestURI.startsWith("/api/tenants") &&  // Gestion des tenants
               requestURI.startsWith("/api/");
    }
    
    /**
     * Gère le cas où aucun tenant n'est fourni
     */
    private void handleMissingTenant(HttpServletResponse response, String requestURI) 
            throws IOException {
        logger.warn("Aucun tenant fourni pour la requête: {}", requestURI);
        
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Header %s requis\",\"message\":\"Veuillez fournir un identifiant de tenant valide\"}", 
            TENANT_HEADER_NAME
        ));
    }
    
    /**
     * Gère le cas où le tenant fourni est invalide
     */
    private void handleInvalidTenant(HttpServletResponse response, String tenantId, String requestURI) 
            throws IOException {
        logger.warn("Tenant invalide '{}' pour la requête: {}", tenantId, requestURI);
        
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Tenant ID invalide\",\"tenantId\":\"%s\",\"message\":\"Le tenant doit contenir 3-50 caractères alphanumériques, tirets ou underscores\"}", 
            tenantId
        ));
    }
}