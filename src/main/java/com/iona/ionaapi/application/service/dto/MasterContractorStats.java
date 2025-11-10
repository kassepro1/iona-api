package com.iona.ionaapi.application.service.dto;


public class MasterContractorStats {
    private final long privateCount;
    private final long publicCount;

    public MasterContractorStats(long privateCount, long publicCount) {
        this.privateCount = privateCount;
        this.publicCount = publicCount;
    }

    // Getters
    public long getPrivateCount() {
        return privateCount;
    }

    public long getPublicCount() {
        return publicCount;
    }

    public long getTotalCount() {
        return privateCount + publicCount;
    }

    @Override
    public String toString() {
        return String.format("MasterContractorStats{private=%d, public=%d, total=%d}",
                privateCount, publicCount, getTotalCount());
    }
}