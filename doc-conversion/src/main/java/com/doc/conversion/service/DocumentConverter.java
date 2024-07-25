package com.doc.conversion.service;

import com.doc.conversion.exception.DocumentNotFound;
import com.doc.conversion.exception.InvalidDocumentException;

import java.io.IOException;

public interface DocumentConverter {
    void convertTo(Long documentId) throws IOException, InvalidDocumentException, DocumentNotFound;

}
