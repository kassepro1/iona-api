package com.iona.ionaapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iona.ionaapi.domaine.InsuranceCertificateDto;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


@Service
@Slf4j
//@RequiredArgsConstructor
public class MistralAiService {

    private final ChatModel mistralAiChatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public MistralAiService(ChatModel chatModel) {
        this.mistralAiChatModel = chatModel;
    }


    public String generateInsuranceCertificate(String insuranceContent,String additionalInstructions) throws ExecutionException, InterruptedException {
        String EXTRACT_INSURANCE_CERTIFICATE_PROMPT = """
                
                Tu es un expert en assurance construction en France spécialisé dans l'analyse et la vérification d'attestations d'assurance décennale.
                Ta mission est d'extraire des informations précises de ces documents et de vérifier leur validité selon les critères légaux.
                Sois méthodique, précis et exhaustif dans ton analyse. Réponds uniquement en français.
                
                Voici le contenu textuel d'un document d'assurance à analyser en profondeur:
                {{INSURANCE_CERTIFICATE_CONTENT}}
                Analyse ce document et extrais les informations suivantes. Tu dois ABSOLUMENT répondre au format JSON structuré comme demandé ci-dessous:
                
                1. decennialCertificate: S'agit-il d'une attestation d'assurance décennale? (true/false)
                2. companyName: Nom complet de l'entreprise assurée (très important, cherche "assuré", "souscripteur", "entreprise" dans le document)
                3. siretNumber: Numéro SIRET complet de l'entreprise (14 chiffres) ou SIREN (9 chiffres)
                4. insurerName: Nom complet de la compagnie d'assurance (très important, cherche "assureur", "compagnie d'assurance", souvent en en-tête)
                5. insurerAddress: Adresse complète de la compagnie d'assurance
                6. policyNumber: Numéro de contrat/police (très important, cherche "numéro de contrat", "numéro de police", "n° de contrat", "n° client")
                7. coveredActivities: Liste détaillée des activités professionnelles garanties
                8. coverageAmounts: Montants des garanties par type de sinistre
                9. startDate: Date de début de validité (format JJ/MM/AAAA)
                10. endDate: Date de fin de validité (format JJ/MM/AAAA)
                11. limitations: Restrictions importantes mentionnées dans l'attestation
                12. legallyCompliant: Le document respecte-t-il les exigences légales? (true/false)
                13. legalComplianceDetails: Détails sur la conformité légale
                
                {{ADDITIONAL_INSTRUCTIONS}}
                
                Ta réponse doit être au format JSON structuré comme ceci:
                json
                {
                  "decennialCertificate": true,
                  "companyName": "SARL Bâtiments Durables",
                  "siretNumber": "12345678901234",
                  "insurerName": "AXA Assurance",
                  "insurerAddress": "10 Rue de la Paix, 75002 Paris, France",
                  "policyNumber": "POL123456789",
                  "coveredActivities": [
                    {
                      "activity": "Construction de maisons individuelles"
                    },
                    {
                      "activity": "Rénovation de bâtiments"
                    }
                  ],
                  "coverageAmounts": [
                    {
                      "guaranteeType": "structure_gros_oeuvre",
                      "amount": "10 000 000 € par sinistre"
                    },
                    {
                      "guaranteeType": "sans_structure_gros_oeuvre",
                      "amount": "6 000 000 € par sinistre"
                    },
                    {
                      "guaranteeType": "concepteur_non_realisation",
                      "amount": "3 000 000 € par sinistre"
                    }
                  ],
                  "startDate": "01/01/2025",
                  "endDate": "31/12/2025",
                  "limitations": [
                    "Exclusion dommages liés aux catastrophes naturelles",
                    "Exclusion travaux hors France"
                  ],
                  "legallyCompliant": true,
                  "legalComplianceDetails": "Le document respecte les exigences légales françaises pour l'assurance décennale."
                }
                
                
                **INSTRUCTIONS IMPORTANTES:**
                1. Ne laisse aucun champ vide ou null. Si une information n'est pas explicitement présente, indique "Non précisé".
                2. Pour nom_entreprise, nom_assureur et numero_contrat, fais un effort particulier pour les extraire, ces informations sont CRITIQUES.
                3. Le nom_entreprise (nom de l'entreprise assurée) se trouve généralement après "assuré:" ou "souscripteur:" ou "entreprise:".
                4. Le nom_assureur est le nom de la compagnie d'assurance (ex: AXA, MAAF, SMABTP) et NON l'entreprise assurée.
                5. Le numero_contrat est généralement un code alphanumérique précédé de "Contrat n°" ou "Police n°".
                
                N'inclus AUCUN texte en dehors du JSON. Ta réponse doit contenir uniquement un objet JSON valide, sans préfixe, sans explication supplémentaire.\s
                Vérifie que ton JSON est bien formé et valide avant de répondre.
                """;
        String finalPrompt = EXTRACT_INSURANCE_CERTIFICATE_PROMPT
                .replace("{{INSURANCE_CERTIFICATE_CONTENT}}", insuranceContent)
                .replace("{{ADDITIONAL_INSTRUCTIONS}}", additionalInstructions);
        return mistralAiChatModel.chat(finalPrompt);
    }

    public InsuranceCertificateDto getInsuranceCertificateFromLLm(String insuranceContent,String additionalInstructions) throws ExecutionException, InterruptedException {
        return parseResult(generateInsuranceCertificate(insuranceContent,additionalInstructions));
    }



    private InsuranceCertificateDto parseResult(String result) {
        try {
            String cleanedJson = result.strip();
            log.info("Insurance certificate from LLm JSON:\n{}", cleanedJson);
            return objectMapper.readValue(cleanedJson, InsuranceCertificateDto.class);
        } catch (Exception e) {
            log.info("Error during parsing invoice");
            e.printStackTrace();
        }
        return null;
    }
}
