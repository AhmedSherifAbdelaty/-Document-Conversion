package com.doc.conversion.service;

import com.doc.conversion.dto.DocumentUploadResponse;
import com.doc.conversion.entity.Document;
import com.doc.conversion.entity.DocumentConversionProgress;
import com.doc.conversion.enumration.DocumentStatus;
import com.doc.conversion.enumration.FileStatus;
import com.doc.conversion.exception.DocumentNotFound;
import com.doc.conversion.exception.InvalidDocumentException;
import com.doc.conversion.repository.DocumentConversionProgressRepository;
import com.doc.conversion.repository.DocumentRepository;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentConversionProgressRepository documentConversionProgressRepository;
    private final FileService fileService;

    public DocumentUploadResponse uploadDocument(MultipartFile file) throws InvalidDocumentException, IOException {
            File uploadedFile = fileService.uploadFile(file);
            Document document = documentRepository.save(DocumentServiceHelper.getUploadedDocument(file, uploadedFile));
            log.info("Document uploaded successfully");
            return new DocumentUploadResponse(document.getId(),document.getStatus());
    }


    public Resource downloadDocument(Long fileId) throws MalformedURLException, InvalidDocumentException {
        Document document = documentRepository.findById(fileId)
                .orElseThrow(() -> new InvalidDocumentException("Document not found"));
        Optional<Resource> resource = fileService.getResource(document.getConvertedFilePath());

        if ( resource.isPresent() && resource.get().exists()) {
            return resource.get();
        } else {
            throw new InvalidDocumentException("Document not found");
        }
    }

    public String getDocumentStatus(Long documentId) {
        DocumentConversionProgress documentConversionProgress = documentConversionProgressRepository.findFirstByDocumentIdOrderByCreatedAtDesc(documentId);
        return documentConversionProgress.getStatus();
    }
}