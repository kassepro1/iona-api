package com.iona.ionaapi.application.service;

import com.iona.ionaapi.application.service.dto.MasterContractorStats;
import com.iona.ionaapi.domain.mastercontractor.MasterContractor;
import com.iona.ionaapi.domain.mastercontractor.enums.MasterContractorType;
import com.iona.ionaapi.infrastructure.repository.MasterContractorRepository;
import com.iona.ionaapi.infrastructure.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Business service for MasterContractor management
 * All operations are performed in the current tenant context
 */
@Service
@Transactional
public class MasterContractorService {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterContractorService.class);
    
    private final MasterContractorRepository masterContractorRepository;
    
    public MasterContractorService(MasterContractorRepository masterContractorRepository) {
        this.masterContractorRepository = masterContractorRepository;
    }
    
    /**
     * Creates a new master contractor
     */
    public MasterContractor createMasterContractor(MasterContractor masterContractor) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Creating master contractor '{}' for tenant: {}", masterContractor.getName(), tenant);
        
        // Validate that SIRET doesn't already exist
        if (masterContractorRepository.existsBySiret(masterContractor.getSiret())) {
            throw new IllegalArgumentException("A master contractor with SIRET " + masterContractor.getSiret() + " already exists");
        }
        
        MasterContractor savedMasterContractor = masterContractorRepository.save(masterContractor);
        logger.info("Master contractor created with ID: {} for tenant: {}", savedMasterContractor.getId(), tenant);
        
        return savedMasterContractor;
    }
    
    /**
     * Updates an existing master contractor
     */
    public MasterContractor updateMasterContractor(UUID id, MasterContractor masterContractor) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Updating master contractor {} for tenant: {}", id, tenant);
        
        Optional<MasterContractor> existingMasterContractor = masterContractorRepository.findById(id);
        if (existingMasterContractor.isEmpty()) {
            throw new IllegalArgumentException("Master contractor not found: " + id);
        }
        
        MasterContractor toUpdate = existingMasterContractor.get();
        
        // Update fields
        if (masterContractor.getName() != null) {
            toUpdate.setName(masterContractor.getName());
        }
        if (masterContractor.getType() != null) {
            toUpdate.setType(masterContractor.getType());
        }
        if (masterContractor.getAddress() != null) {
            toUpdate.setAddress(masterContractor.getAddress());
        }
        // Note: SIRET update should be carefully handled in real applications
        if (masterContractor.getSiret() != null && !masterContractor.getSiret().equals(toUpdate.getSiret())) {
            if (masterContractorRepository.existsBySiret(masterContractor.getSiret())) {
                throw new IllegalArgumentException("A master contractor with SIRET " + masterContractor.getSiret() + " already exists");
            }
            toUpdate.setSiret(masterContractor.getSiret());
        }
        
        MasterContractor updatedMasterContractor = masterContractorRepository.save(toUpdate);
        logger.info("Master contractor {} updated for tenant: {}", id, tenant);
        
        return updatedMasterContractor;
    }
    
    /**
     * Retrieves a master contractor by ID
     */
    @Transactional(readOnly = true)
    public Optional<MasterContractor> getMasterContractorById(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching for master contractor {} for tenant: {}", id, tenant);
        
        return masterContractorRepository.findById(id);
    }
    
    /**
     * Lists all master contractors with pagination
     */
    @Transactional(readOnly = true)
    public Page<MasterContractor> getAllMasterContractors(Pageable pageable) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving all master contractors for tenant: {}", tenant);
        
        return masterContractorRepository.findAll(pageable);
    }
    
    /**
     * Searches master contractors by name
     */
    @Transactional(readOnly = true)
    public List<MasterContractor> searchMasterContractorsByName(String name) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching master contractors by name '{}' for tenant: {}", name, tenant);
        
        return masterContractorRepository.searchByName(name);
    }
    
    /**
     * Retrieves master contractors by type
     */
    @Transactional(readOnly = true)
    public List<MasterContractor> getMasterContractorsByType(MasterContractorType type) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving master contractors of type {} for tenant: {}", type, tenant);
        
        return masterContractorRepository.findByType(type);
    }
    
    /**
     * Deletes a master contractor permanently
     */
    public void deleteMasterContractor(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.warn("Permanently deleting master contractor {} for tenant: {}", id, tenant);
        
        if (!masterContractorRepository.existsById(id)) {
            throw new IllegalArgumentException("Master contractor not found: " + id);
        }
        
        masterContractorRepository.deleteById(id);
        logger.warn("Master contractor {} permanently deleted for tenant: {}", id, tenant);
    }
    
    /**
     * Master contractor statistics by type for current tenant
     */
    @Transactional(readOnly = true)
    public MasterContractorStats getMasterContractorStats() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Generating master contractor stats for tenant: {}", tenant);
        
        return new MasterContractorStats(
            masterContractorRepository.countByType(MasterContractorType.PRIVATE),
            masterContractorRepository.countByType(MasterContractorType.PUBLIC)
        );
    }

}