package com.iona.ionaapi.application.service.dto;

/**
 * Classe pour les statistiques
 */
public class PartnerStats {
    private final long brokerCount;
    private final long insurerCount;
    private final long masterContractorCount;
    private final long contractorCount;

    public PartnerStats(long brokerCount, long insurerCount, long masterContractorCount, long contractorCount) {
        this.brokerCount = brokerCount;
        this.insurerCount = insurerCount;
        this.masterContractorCount = masterContractorCount;
        this.contractorCount = contractorCount;
    }

    // Getters
    public long getBrokerCount() {
        return brokerCount;
    }

    public long getInsurerCount() {
        return insurerCount;
    }

    public long getMasterContractorCount() {
        return masterContractorCount;
    }

    public long getContractorCount() {
        return contractorCount;
    }

    public long getTotalCount() {
        return brokerCount + insurerCount + masterContractorCount + contractorCount;
    }

    @Override
    public String toString() {
        return String.format("PartnerStats{brokers=%d, insurers=%d, masterContractors=%d, contractors=%d, total=%d}",
                brokerCount, insurerCount, masterContractorCount, contractorCount, getTotalCount());
    }
}