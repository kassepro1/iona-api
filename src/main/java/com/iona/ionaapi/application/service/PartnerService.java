package com.iona.ionaapi.application.service;

import com.iona.ionaapi.application.service.dto.PartnerStats;
import com.iona.ionaapi.domain.partner.enums.PartnerStatus;
import com.iona.ionaapi.domain.partner.enums.PartnerType;
import com.iona.ionaapi.domain.partner.Partner;
import com.iona.ionaapi.infrastructure.repository.PartnerRepository;
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
 * Service métier pour la gestion des Partners
 * Toutes les opérations se font dans le contexte du tenant courant
 */
@Service
@Transactional
public class PartnerService {
    
    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);
    
    private final PartnerRepository partnerRepository;
    
    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }
    
    /**
     * Crée un nouveau partner
     */
    public Partner createPartner(Partner partner) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Création d'un partner '{}' pour le tenant: {}", partner.getName(), tenant);
        
        // Valide que le SIRET n'existe pas déjà
        if (partnerRepository.existsBySiret(partner.getSiret())) {
            throw new IllegalArgumentException("Un partner avec le SIRET " + partner.getSiret() + " existe déjà");
        }
        
        Partner savedPartner = partnerRepository.save(partner);
        logger.info("Partner créé avec l'ID: {} pour le tenant: {}", savedPartner.getId(), tenant);
        
        return savedPartner;
    }
    
    /**
     * Met à jour un partner existant
     */
    public Partner updatePartner(UUID id, Partner partner) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Mise à jour du partner {} pour le tenant: {}", id, tenant);
        
        Optional<Partner> existingPartner = partnerRepository.findById(id);
        if (existingPartner.isEmpty()) {
            throw new IllegalArgumentException("Partner non trouvé: " + id);
        }
        
        Partner toUpdate = existingPartner.get();
        
        // Met à jour les champs
        if (partner.getName() != null) {
            toUpdate.setName(partner.getName());
        }
        if (partner.getContactEmail() != null) {
            toUpdate.setContactEmail(partner.getContactEmail());
        }
        if (partner.getPhone() != null) {
            toUpdate.setPhone(partner.getPhone());
        }
        if (partner.getAddress() != null) {
            toUpdate.setAddress(partner.getAddress());
        }
        if (partner.getStatus() != null) {
            toUpdate.setStatus(partner.getStatus());
        }
        
        Partner updatedPartner = partnerRepository.save(toUpdate);
        logger.info("Partner {} mis à jour pour le tenant: {}", id, tenant);
        
        return updatedPartner;
    }
    
    /**
     * Récupère un partner par ID
     */
    @Transactional(readOnly = true)
    public Optional<Partner> getPartnerById(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Recherche du partner {} pour le tenant: {}", id, tenant);
        
        return partnerRepository.findById(id);
    }
    
    /**
     * Liste tous les partners avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Partner> getAllPartners(Pageable pageable) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Récupération de tous les partners pour le tenant: {}", tenant);
        
        return partnerRepository.findAll(pageable);
    }
    
    /**
     * Recherche des partners par nom
     */
    @Transactional(readOnly = true)
    public List<Partner> searchPartnersByName(String name) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Recherche de partners par nom '{}' pour le tenant: {}", name, tenant);
        
        return partnerRepository.searchByName(name);
    }
    
    /**
     * Récupère les partners par type
     */
    @Transactional(readOnly = true)
    public List<Partner> getPartnersByType(PartnerType type) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Récupération des partners de type {} pour le tenant: {}", type, tenant);
        
        return partnerRepository.findByPartnerType(type);
    }
    
    /**
     * Archive un partner (soft delete)
     */
    public void archivePartner(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.info("Archivage du partner {} pour le tenant: {}", id, tenant);
        
        Optional<Partner> partner = partnerRepository.findById(id);
        if (partner.isEmpty()) {
            throw new IllegalArgumentException("Partner non trouvé: " + id);
        }
        
        Partner toArchive = partner.get();
        toArchive.setStatus(PartnerStatus.ARCHIVED);
        partnerRepository.save(toArchive);
        
        logger.info("Partner {} archivé pour le tenant: {}", id, tenant);
    }
    
    /**
     * Supprime définitivement un partner
     */
    public void deletePartner(UUID id) {
        String tenant = TenantContext.getTenantOrDefault();
        logger.warn("Suppression définitive du partner {} pour le tenant: {}", id, tenant);
        
        if (!partnerRepository.existsById(id)) {
            throw new IllegalArgumentException("Partner non trouvé: " + id);
        }
        
        partnerRepository.deleteById(id);
        logger.warn("Partner {} supprimé définitivement pour le tenant: {}", id, tenant);
    }
    
    /**
     * Statistiques des partners par type pour le tenant courant
     */
    @Transactional(readOnly = true)
    public PartnerStats getPartnerStats() {
        String tenant = TenantContext.getTenantOrDefault();
        logger.debug("Génération des statistiques partners pour le tenant: {}", tenant);
        
        return new PartnerStats(
            partnerRepository.countByPartnerType(PartnerType.BROKER),
            partnerRepository.countByPartnerType(PartnerType.INSURER),
            partnerRepository.countByPartnerType(PartnerType.MASTER_CONTRACTOR),
            partnerRepository.countByPartnerType(PartnerType.CONTRACTOR)
        );
    }
    

}