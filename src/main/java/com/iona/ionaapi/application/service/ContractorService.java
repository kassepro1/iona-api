package com.iona.ionaapi.application.service;

import com.iona.ionaapi.domain.contractor.Contractor;

import com.iona.ionaapi.domain.contractor.enums.ContractorSpecialty;
import com.iona.ionaapi.domain.contractor.enums.ContractorStatus;
import com.iona.ionaapi.domain.contractor.enums.ContractorType;
import com.iona.ionaapi.infrastructure.repository.ContractorRepository;
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
 * Business service for Contractor management
 * All operations are performed in the current tenant context
 */
@Service
@Transactional
public class ContractorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContractorService.class);
    
    private final ContractorRepository contractorRepository;
    
    public ContractorService(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }
    
    /**
     * Creates a new contractor
     */
    public Contractor createContractor(Contractor contractor) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Creating contractor '{}' for tenant: {}", contractor.getName(), tenant);
        
        // Validate that SIRET doesn't already exist
        if (contractorRepository.existsBySiret(contractor.getSiret())) {
            throw new IllegalArgumentException("A contractor with SIRET " + contractor.getSiret() + " already exists");
        }
        
        // Ensure at least one specialty
        if (contractor.getSpecialties().isEmpty()) {
            contractor.addSpecialty(ContractorSpecialty.OTHER);
            logger.info("No specialty provided, added OTHER as default for contractor: {}", contractor.getName());
        }
        
        Contractor savedContractor = contractorRepository.save(contractor);
        logger.info("Contractor created with ID: {} for tenant: {} with {} specialties", 
                   savedContractor.getId(), tenant, savedContractor.getSpecialties().size());
        
        return savedContractor;
    }
    
    /**
     * Updates an existing contractor
     */
    public Contractor updateContractor(UUID id, Contractor contractor) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Updating contractor {} for tenant: {}", id, tenant);
        
        Optional<Contractor> existingContractor = contractorRepository.findById(id);
        if (existingContractor.isEmpty()) {
            throw new IllegalArgumentException("Contractor not found: " + id);
        }
        
        Contractor toUpdate = existingContractor.get();
        
        // Update basic fields
        if (contractor.getName() != null) {
            toUpdate.setName(contractor.getName());
        }
        if (contractor.getType() != null) {
            toUpdate.setType(contractor.getType());
        }
        if (contractor.getAddress() != null) {
            toUpdate.setAddress(contractor.getAddress());
        }
        if (contractor.getStatus() != null) {
            toUpdate.setStatus(contractor.getStatus());
        }
        
        // Handle SIRET update carefully
        if (contractor.getSiret() != null && !contractor.getSiret().equals(toUpdate.getSiret())) {
            if (contractorRepository.existsBySiret(contractor.getSiret())) {
                throw new IllegalArgumentException("A contractor with SIRET " + contractor.getSiret() + " already exists");
            }
            toUpdate.setSiret(contractor.getSiret());
        }
        
        // Update specialties if provided
        if (contractor.getSpecialties() != null && !contractor.getSpecialties().isEmpty()) {
            toUpdate.getSpecialties().clear();
            toUpdate.getSpecialties().addAll(contractor.getSpecialties());
            logger.info("Updated {} specialties for contractor {}", contractor.getSpecialties().size(), id);
        }
        
        // Update contacts if provided
        if (contractor.getContacts() != null) {
            toUpdate.getContacts().clear();
            for (var contact : contractor.getContacts()) {
                toUpdate.addContact(contact);
            }
            logger.info("Updated {} contacts for contractor {}", contractor.getContacts().size(), id);
        }
        
        Contractor updatedContractor = contractorRepository.save(toUpdate);
        logger.info("Contractor {} updated for tenant: {}", id, tenant);
        
        return updatedContractor;
    }
    
    /**
     * Retrieves a contractor by ID
     */
    @Transactional(readOnly = true)
    public Optional<Contractor> getContractorById(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching for contractor {} for tenant: {}", id, tenant);
        
        return contractorRepository.findById(id);
    }
    
    /**
     * Lists all contractors with pagination
     */
    @Transactional(readOnly = true)
    public Page<Contractor> getAllContractors(Pageable pageable) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving all contractors for tenant: {}", tenant);
        
        return contractorRepository.findAll(pageable);
    }
    
    /**
     * Searches contractors by name
     */
    @Transactional(readOnly = true)
    public List<Contractor> searchContractorsByName(String name) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Searching contractors by name '{}' for tenant: {}", name, tenant);
        
        return contractorRepository.searchByName(name);
    }
    
    /**
     * Retrieves contractors by type
     */
    @Transactional(readOnly = true)
    public List<Contractor> getContractorsByType(ContractorType type) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving contractors of type {} for tenant: {}", type, tenant);
        
        return contractorRepository.findByType(type);
    }
    
    /**
     * Retrieves contractors by specialty
     */
    @Transactional(readOnly = true)
    public List<Contractor> getContractorsBySpecialty(ContractorSpecialty specialty) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving contractors with specialty {} for tenant: {}", specialty, tenant);
        
        return contractorRepository.findBySpecialty(specialty);
    }
    
    /**
     * Retrieves contractors by multiple specialties
     */
    @Transactional(readOnly = true)
    public List<Contractor> getContractorsBySpecialties(List<ContractorSpecialty> specialties) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Retrieving contractors with specialties {} for tenant: {}", specialties, tenant);
        
        return contractorRepository.findBySpecialtiesIn(specialties);
    }
    
    /**
     * Archive a contractor (soft delete)
     */
    public void archiveContractor(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Archiving contractor {} for tenant: {}", id, tenant);
        
        Optional<Contractor> contractor = contractorRepository.findById(id);
        if (contractor.isEmpty()) {
            throw new IllegalArgumentException("Contractor not found: " + id);
        }
        
        Contractor toArchive = contractor.get();
        toArchive.setStatus(ContractorStatus.ARCHIVED);
        contractorRepository.save(toArchive);
        
        logger.info("Contractor {} archived for tenant: {}", id, tenant);
    }
    
    /**
     * Blacklist a contractor
     */
    public void blacklistContractor(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.warn("Blacklisting contractor {} for tenant: {}", id, tenant);
        
        Optional<Contractor> contractor = contractorRepository.findById(id);
        if (contractor.isEmpty()) {
            throw new IllegalArgumentException("Contractor not found: " + id);
        }
        
        Contractor toBlacklist = contractor.get();
        toBlacklist.setStatus(ContractorStatus.BLACKLISTED);
        contractorRepository.save(toBlacklist);
        
        logger.warn("Contractor {} blacklisted for tenant: {}", id, tenant);
    }
    
    /**
     * Get contractor statistics by type and specialty for current tenant
     */
    @Transactional(readOnly = true)
    public ContractorStats getContractorStats() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Generating contractor stats for tenant: {}", tenant);
        
        return new ContractorStats(
            contractorRepository.countByType(ContractorType.GENERAL_CONTRACTOR),
            contractorRepository.countByType(ContractorType.SUBCONTRACTOR),
            contractorRepository.countByType(ContractorType.SPECIALIST),
            contractorRepository.countByType(ContractorType.CONSULTANT),
            contractorRepository.count(),
            contractorRepository.findActiveContractors().size()
        );
    }
    
    /**
     * Statistics class
     */
    public static class ContractorStats {
        private final long generalContractorCount;
        private final long subcontractorCount;
        private final long specialistCount;
        private final long consultantCount;
        private final long totalCount;
        private final long activeCount;
        
        public ContractorStats(long generalContractorCount, long subcontractorCount, 
                             long specialistCount, long consultantCount, 
                             long totalCount, long activeCount) {
            this.generalContractorCount = generalContractorCount;
            this.subcontractorCount = subcontractorCount;
            this.specialistCount = specialistCount;
            this.consultantCount = consultantCount;
            this.totalCount = totalCount;
            this.activeCount = activeCount;
        }
        
        // Getters
        public long getGeneralContractorCount() { return generalContractorCount; }
        public long getSubcontractorCount() { return subcontractorCount; }
        public long getSpecialistCount() { return specialistCount; }
        public long getConsultantCount() { return consultantCount; }
        public long getTotalCount() { return totalCount; }
        public long getActiveCount() { return activeCount; }
        
        @Override
        public String toString() {
            return String.format("ContractorStats{general=%d, sub=%d, specialist=%d, consultant=%d, total=%d, active=%d}", 
                               generalContractorCount, subcontractorCount, specialistCount, consultantCount, totalCount, activeCount);
        }
    }
}