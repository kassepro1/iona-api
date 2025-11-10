package com.iona.ionaapi.controller;

import com.iona.ionaapi.domain.InsuranceCertificateDto;
import com.iona.ionaapi.service.MistralAiService;
import com.iona.ionaapi.service.PdfExtractorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class AnalyzerController {


    private final List<InsuranceCertificateDto> insuranceCertificateDtos = new ArrayList<>();

    private final PdfExtractorService pdfExtractorService;
    private final MistralAiService  mistralAiService;

    @PostMapping("/analyze")
    public ResponseEntity<InsuranceCertificateDto> analyze(
            @RequestParam("pdf") MultipartFile pdfFile,
            HttpServletRequest httpServletRequest) throws Exception {
        String xApiKey = httpServletRequest.getHeader("x-api-key");
        Resource pdf = pdfFile.getResource();
        String content = "";
        log.info("Loading data from pdf file {}", pdfFile.getOriginalFilename());
         content = pdfExtractorService.extractPage(pdf,1);
         content+=  pdfExtractorService.extractPage(pdf,2);
        log.info("Extracted content from pdf file {}",content);
        InsuranceCertificateDto insuranceCertificateDto = mistralAiService.getInsuranceCertificateFromLLm(content,"");
        insuranceCertificateDtos.add(insuranceCertificateDto);
        return new  ResponseEntity<>(insuranceCertificateDto, HttpStatus.OK);
    }


    @GetMapping("/insurances")
    public List<InsuranceCertificateDto> getInsuranceCertificates() {
        return insuranceCertificateDtos;
    }
}
