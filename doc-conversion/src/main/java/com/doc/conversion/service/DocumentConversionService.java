package com.doc.conversion.service;

import com.doc.conversion.exception.DocumentNotFound;
import com.doc.conversion.exception.InvalidDocumentException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class DocumentConversionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentConversionService.class);
    private final DocumentConverterFactory documentConverterFactory;


    public void convertDocument(Long documentId, String documentType) throws IOException, DocumentNotFound, InvalidDocumentException {
        DocumentConverter converter = documentConverterFactory.getConverter(documentType);
        if (converter != null) {
                converter.convertTo(documentId);
        } else {
            log.error("No suitable converter found for type {}", documentType);
        }
    }
}