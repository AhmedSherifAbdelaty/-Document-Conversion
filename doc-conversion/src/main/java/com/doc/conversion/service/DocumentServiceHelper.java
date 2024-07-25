package com.doc.conversion.service;

import com.doc.conversion.entity.Document;
import com.doc.conversion.entity.DocumentConversionProgress;
import com.doc.conversion.enumration.DocumentStatus;
import com.doc.conversion.enumration.FileStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

public class DocumentServiceHelper {

    public static Document getUploadedDocument(MultipartFile file, File uploadedFile) {
        return Document.builder()
                .originalFilename(file.getOriginalFilename())
                .uploadedFileName(uploadedFile.getName())
                .uploadedFilePath(uploadedFile.getAbsolutePath())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .status(FileStatus.UPLOADED.name())
                .build();
    }

    public  static DocumentConversionProgress getInProgressDocumentStatusEntity(Long documentId) {
        return
                DocumentConversionProgress.builder()
                        .documentId(documentId)
                        .conversionType("DocxToPDF")
                        .status(DocumentStatus.IN_PROGRESS.toString())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

    }
}
