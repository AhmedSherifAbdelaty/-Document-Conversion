package com.doc.conversion.controller;

import com.doc.conversion.dto.DocumentConversionRequest;
import com.doc.conversion.dto.DocumentUploadResponse;
import com.doc.conversion.exception.DocumentNotFound;
import com.doc.conversion.exception.InvalidDocumentException;
import com.doc.conversion.message_queue.RabbitMQProducer;
import com.doc.conversion.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DocumentControllerTest {

    @InjectMocks
    private DocumentController documentController;

    @Mock
    private DocumentService documentService;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadDocument() throws InvalidDocumentException, IOException {
        MockMultipartFile file = new MockMultipartFile("file",  new ByteArrayInputStream("content".getBytes()));
        DocumentUploadResponse response = new DocumentUploadResponse(1L, "Uploaded");
        when(documentService.uploadDocument(file)).thenReturn(response);

        ResponseEntity<DocumentUploadResponse> result = documentController.uploadDocument(file);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void testConvertDocument() {
        DocumentConversionRequest request = new DocumentConversionRequest();
        doNothing().when(rabbitMQProducer).send(request);

        ResponseEntity<String> result = documentController.convertDocument(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(rabbitMQProducer, times(1)).send(request);
    }

    @Test
    void testGetDocumentStatus() throws DocumentNotFound {
        Long documentId = 1L;
        String status = "completed";
        when(documentService.getDocumentStatus(documentId)).thenReturn(status);

        ResponseEntity<String> result = documentController.getDocumentStatus(documentId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(status, result.getBody());
    }

    @Test
    void testDownloadFile() throws InvalidDocumentException, IOException {
        Long documentId = 1L;
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("document.pdf");
        when(documentService.downloadDocument(documentId)).thenReturn(resource);

        ResponseEntity<Resource> result = documentController.downloadFile(documentId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resource, result.getBody());
    }

    @Test
    void testUploadDocument_InvalidDocumentException() throws InvalidDocumentException, IOException {
        MockMultipartFile file = new MockMultipartFile("file",  new ByteArrayInputStream("content".getBytes()));
        when(documentService.uploadDocument(file)).thenThrow(new InvalidDocumentException("Invalid document"));

        assertThrows(InvalidDocumentException.class, () -> documentController.uploadDocument(file));
    }

    @Test
    void testDownloadFile_InvalidDocumentException() throws InvalidDocumentException, IOException {
        Long documentId = 1L;
        when(documentService.downloadDocument(documentId)).thenThrow(new InvalidDocumentException("Invalid document"));
        assertThrows(InvalidDocumentException.class, () -> documentController.downloadFile(documentId));
    }
}
