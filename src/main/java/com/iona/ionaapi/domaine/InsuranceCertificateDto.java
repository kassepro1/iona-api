package com.iona.ionaapi.domaine;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InsuranceCertificateDto {
    private boolean decennialCertificate;
    private String companyName;
    private String siretNumber;
    private String insurerName;
    private String insurerAddress;
    private String policyNumber;
    private List<CoveredActivityDto> coveredActivities = new ArrayList<>();
    private List<CoverageAmountDto> coverageAmounts = new ArrayList<>();
    private String startDate;
    private String endDate;
    private List<String> limitations = new ArrayList<>();
    private boolean legallyCompliant;
    private String legalComplianceDetails;
}
