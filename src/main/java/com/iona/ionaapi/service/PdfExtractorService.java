package com.iona.ionaapi.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PdfExtractorService {

    public String loadDataIntoVectorStore(Resource pdfResource) throws IOException, IOException {
        PDDocument document = PDDocument.load(pdfResource.getInputStream());
        PDPageTree pdPages = document.getPages();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        int page = 0;
        System.out.println("pdPages size  " + pdPages.getCount());
        for (PDPage pdPage : pdPages) {
            ++page;
            pdfTextStripper.setStartPage(page);
            pdfTextStripper.setEndPage(page);
            PDResources resources = pdPage.getResources();
            List<String> media = new ArrayList<>();
            String textContent = pdfTextStripper.getText(document);
            log.info("textContent " + textContent);
            if(page==0){
                return textContent;
            }
        }
        return null;
    }

    public String extractPage(Resource pdfResource, int pageNumber) throws IOException {
        try (PDDocument document = PDDocument.load(pdfResource.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

}
