    package com.iona.ionaapi.infrastructure.dto.projet.projet;

/**
 * Project document upload request
 */
public class ProjectDocumentRequest {
    private String documentName;
    private String documentType;
    private String contentType;
    private Long fileSize;
    // Note: Le fichier sera traité séparément via MultipartFile dans le controller
    
    // Constructors
    public ProjectDocumentRequest() {}
    
    // Getters and Setters
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}