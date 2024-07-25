package com.doc.conversion.service;

import com.doc.conversion.dto.DocumentUploadResponse;
import com.doc.conversion.entity.Document;
import com.doc.conversion.entity.DocumentConversionProgress;
import com.doc.conversion.exception.InvalidDocumentException;
import com.doc.conversion.repository.DocumentConversionProgressRepository;
import com.doc.conversion.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentConversionProgressRepository documentConversionProgressRepository;

    @Mock
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadDocument() throws InvalidDocumentException, IOException {
        MockMultipartFile file = new MockMultipartFile("file",  new ByteArrayInputStream("content".getBytes()));
        File uploadedFile = new File("path/to/uploaded/document.docx");
        Document document = new Document();
        document.setId(1L);
        when(fileService.uploadFile(file)).thenReturn(uploadedFile);
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentUploadResponse response = documentService.uploadDocument(file);

        assertEquals(document.getId(), response.getDocumentId());
        assertEquals(document.getStatus(), response.getStatus());
    }

    @Test
    void testDownloadDocument() throws InvalidDocumentException, MalformedURLException, IOException {
        Long documentId = 1L;
        Document document = new Document();
        document.setConvertedFilePath("path/to/document.pdf");
        Resource resource = mock(Resource.class);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(fileService.getResource("path/to/document.pdf")).thenReturn(Optional.of(resource));
        when(resource.exists()).thenReturn(true);

        Resource result = documentService.downloadDocument(documentId);

        assertEquals(resource, result);
    }

    @Test
    void testDownloadDocument_DocumentNotFound() {
        Long documentId = 1L;
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(InvalidDocumentException.class, () -> documentService.downloadDocument(documentId));
    }

    @Test
    void testGetDocumentStatus() {
        Long documentId = 1L;
        DocumentConversionProgress progress = new DocumentConversionProgress();
        progress.setStatus("completed");
        when(documentConversionProgressRepository.findFirstByDocumentIdOrderByCreatedAtDesc(documentId)).thenReturn(progress);

        String status = documentService.getDocumentStatus(documentId);

        assertEquals("completed", status);
    }
}
