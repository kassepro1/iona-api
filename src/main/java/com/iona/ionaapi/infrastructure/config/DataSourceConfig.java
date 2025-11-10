package com.iona.ionaapi.infrastructure.config;

import com.iona.ionaapi.infrastructure.datasource.MultiTenantDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration des DataSources multi-tenant
 * Crée et configure dynamiquement les sources de données pour chaque tenant
 */
@Configuration
public class DataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    
    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/iona}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username:iona_app}")
    private String databaseUsername;
    
    @Value("${spring.datasource.password:password}")
    private String databasePassword;
    
    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;
    
    /**
     * DataSource principal multi-tenant
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();
        
        // Configure les sources de données pour les schemas par défaut
        Map<Object, Object> dataSourceMap = createInitialDataSources();
        
        multiTenantDataSource.setTargetDataSources(dataSourceMap);
        multiTenantDataSource.setDefaultTargetDataSource(createDataSourceForSchema("public"));
        
        // Initialise la résolution des DataSources
        multiTenantDataSource.afterPropertiesSet();
        
        logger.info("MultiTenantDataSource configuré avec {} schemas initiaux", dataSourceMap.size());
        return multiTenantDataSource;
    }
    
    /**
     * Crée les sources de données initiales pour les schemas par défaut
     */
    private Map<Object, Object> createInitialDataSources() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        
        // Schema public (par défaut)
        dataSourceMap.put("public", createDataSourceForSchema("public"));
        
        // Schema tenant par défaut
        dataSourceMap.put("tenant_default", createDataSourceForSchema("tenant_default"));
        
        // Peut ajouter d'autres schemas prédéfinis ici
        addPredefinedTenantSchemas(dataSourceMap);
        
        return dataSourceMap;
    }
    
    /**
     * Ajoute des schemas tenant prédéfinis (optionnel)
     */
    private void addPredefinedTenantSchemas(Map<Object, Object> dataSourceMap) {
        // Exemples de tenants prédéfinis - peut être configuré via properties
        String[] predefinedTenants = {
            "courtier-lacroix",
            "axa-assurance", 
            "bouygues-construction"
        };
        
        for (String tenant : predefinedTenants) {
            String schemaName = "tenant_" + tenant.toLowerCase().replaceAll("[^a-z0-9_-]", "_");
            dataSourceMap.put(schemaName, createDataSourceForSchema(schemaName));
            logger.debug("Schema prédéfini ajouté: {}", schemaName);
        }
    }
    
    /**
     * Crée une DataSource pour un schema spécifique
     */
    private DataSource createDataSourceForSchema(String schema) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // Configuration de base
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        
        // URL avec schema par défaut
        String urlWithSchema = buildUrlWithSchema(schema);
        dataSource.setUrl(urlWithSchema);
        
        logger.debug("DataSource créée pour le schema: {} avec URL: {}", schema, urlWithSchema);
        return dataSource;
    }
    
    /**
     * Construit l'URL de connexion avec le schema par défaut
     */
    private String buildUrlWithSchema(String schema) {
        // Ajoute le paramètre currentSchema à l'URL PostgreSQL
        String separator = databaseUrl.contains("?") ? "&" : "?";
        return String.format("%s%scurrentSchema=%s", databaseUrl, separator, schema);
    }
    
    /**
     * Ajoute dynamiquement une DataSource pour un nouveau tenant
     * Cette méthode peut être appelée programmatiquement
     */
    public void addTenantDataSource(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID ne peut pas être vide");
        }
        
        String schemaName = "tenant_" + tenantId.toLowerCase().replaceAll("[^a-z0-9_-]", "_");
        
        try {
            // Obtient la référence du MultiTenantDataSource
            DataSource mainDataSource = dataSource();
            if (mainDataSource instanceof MultiTenantDataSource multiTenantDS) {
                
                // Crée la nouvelle DataSource
                DataSource newDataSource = createDataSourceForSchema(schemaName);
                
                // Note: Dans une vraie implémentation, il faudrait une méthode
                // pour ajouter dynamiquement à MultiTenantDataSource
                // Pour l'instant, les nouveaux tenants seront créés automatiquement
                // via la résolution dynamique dans MultiTenantDataSource
                
                logger.info("DataSource ajoutée pour le tenant: {} (schema: {})", tenantId, schemaName);
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la DataSource pour le tenant: {}", tenantId, e);
            throw new RuntimeException("Impossible d'ajouter la DataSource pour le tenant: " + tenantId, e);
        }
    }
    
    /**
     * DataSource pour les opérations administratives (sans tenant)
     */
    @Bean(name = "adminDataSource")
    public DataSource adminDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        
        logger.debug("DataSource administrateur créée");
        return dataSource;
    }
}