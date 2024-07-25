package com.doc.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentUploadResponse {
    private Long documentId;
    private String status;
}
