package com.doc.conversion.service;

import com.doc.conversion.entity.Document;
import com.doc.conversion.entity.DocumentConversionProgress;
import com.doc.conversion.enumration.DocumentStatus;
import com.doc.conversion.enumration.FileStatus;
import com.doc.conversion.repository.DocumentConversionProgressRepository;
import com.doc.conversion.repository.DocumentRepository;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class WordToPDFConverter implements DocumentConverter{

    private final DocumentRepository documentRepository;
    private final DocumentConversionProgressRepository documentConversionProgressRepository;
    private final FileService fileService;
    private final Environment environment ;



    @Override
    public void convertTo(Long documentId) {
        Optional<Document> optionalDocument = findDocumentById(documentId);
        if (optionalDocument.isEmpty()) {
            log.error("Document with ID: {} not found", documentId);
            return;
        }

        Document document = optionalDocument.get();
        DocumentConversionProgress documentConversionProgress = addDocumentStatusInProgress(documentId);

        try {
            validateAndConvertDocument(document, documentConversionProgress);
        } catch (Exception e) {
            handleConversionFailure(documentConversionProgress, documentId, e);
        }
    }

    private void validateAndConvertDocument(Document document, DocumentConversionProgress progress) throws Exception {
        if (!isValidDocumentType(document.getUploadedFilePath())) {
            log.error("Invalid document type: {}", document.getUploadedFilePath());
            throw new IllegalArgumentException("Invalid document type");
        }

        Path pdfFilePath = prepareConversionPaths(document);
        convertDocxToPdf(document.getUploadedFilePath(), pdfFilePath);

        log.info("Successfully converted document ID: {} to PDF", document.getId());

        updateDocumentWithConvertedFile(pdfFilePath, document);
        updateDocumentStatusCompleted(progress.getId());
    }

    private void handleConversionFailure(DocumentConversionProgress progress, Long documentId, Exception e) {
        updateDocumentStatusFailed(progress.getId());
        log.error("Error converting document ID: {} to PDF", documentId);
    }



    private Optional<Document> findDocumentById(Long documentId)  {
        return documentRepository.findById(documentId);
    }
    private DocumentConversionProgress addDocumentStatusInProgress(Long documentId) {
        DocumentConversionProgress documentConversionProgress =  documentConversionProgressRepository.save(DocumentServiceHelper.getInProgressDocumentStatusEntity(documentId));
        log.info("Starting conversion for document ID: {} and status {}", documentId , DocumentStatus.IN_PROGRESS);
        return documentConversionProgress;
    }

    private boolean isValidDocumentType(String filePath) {
        return "docx".equalsIgnoreCase(FilenameUtils.getExtension(filePath));
    }

    private Path prepareConversionPaths(Document document) throws IOException {
        String convertDirectoryPath = System.getProperty("user.dir") + File.separator + environment.getProperty("file.convert.dir");
        Path convertDirectory = fileService.createDirectoryIfNotExists(convertDirectoryPath);
        String pdfFileName = fileService.replaceDocxWithPdf(document.getUploadedFileName());
        return convertDirectory.resolve(pdfFileName);
    }

    private void convertDocxToPdf(String docxFilePath, Path pdfFilePath) throws IOException {
        try (InputStream docxInputStream = fileService.getDocxInputStream(fileService.getDocxPath(docxFilePath));
             XWPFDocument xwpfDocument = new XWPFDocument(docxInputStream);
             OutputStream pdfOutputStream = fileService.getPdfOutputStream(pdfFilePath)) {

            com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();
            PdfWriter.getInstance(pdfDocument, pdfOutputStream);
            pdfDocument.open();

            List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                pdfDocument.add(new Paragraph(paragraph.getText()));
            }
            pdfDocument.close();
        } catch (Exception e) {
            log.error("Error converting DOCX to PDF for file: {}", docxFilePath, e);
        }
    }

    private void updateDocumentWithConvertedFile(Path pdfFilePath,Document document) {
        String convertedFileName = FilenameUtils.getName(pdfFilePath.getFileName().toString());
        String convertedFilePath = pdfFilePath.toAbsolutePath().toString();
        document.setConvertedFilePath(convertedFilePath);
        document.setConvertedFilename(convertedFileName);
        document.setStatus(FileStatus.CONVERTED.toString());
        documentRepository.save(document);
        log.info("Converted document ID: {} to PDF", document.getId());
    }

    private void updateDocumentStatusCompleted(Long id) {
        documentConversionProgressRepository.updateDocumentConversionStatus(id,DocumentStatus.COMPLETED.toString());
        log.info("Document converted successfully");
    }

    private void updateDocumentStatusFailed(Long id) {
        documentConversionProgressRepository.updateDocumentConversionStatus(id,DocumentStatus.FAILED.toString());
        log.info("Document status update failed");
    }

}
