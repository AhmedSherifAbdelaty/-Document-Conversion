package com.doc.conversion.service;

import com.doc.conversion.entity.Document;
import com.doc.conversion.entity.DocumentConversionProgress;
import com.doc.conversion.enumration.DocumentStatus;
import com.doc.conversion.enumration.FileStatus;
import com.doc.conversion.repository.DocumentConversionProgressRepository;
import com.doc.conversion.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WordToPDFConverterTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentConversionProgressRepository documentConversionProgressRepository;

    @Mock
    private FileService fileService;

    @Mock
    private Environment environment;

    @InjectMocks
    private WordToPDFConverter wordToPDFConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertTo_Success() throws Exception {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setUploadedFilePath("test.docx");
        document.setUploadedFileName("test.docx");

        DocumentConversionProgress progress = new DocumentConversionProgress();
        progress.setId(1L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentConversionProgressRepository.save(any())).thenReturn(progress);
        when(fileService.createDirectoryIfNotExists(any())).thenReturn(Path.of("temp"));
        when(fileService.getDocxInputStream(any())).thenReturn(new ByteArrayInputStream("Test Content".getBytes()));
        when(fileService.getPdfOutputStream(any())).thenReturn(new ByteArrayOutputStream());
        when(environment.getProperty("file.convert.dir")).thenReturn("converted");
        when(fileService.replaceDocxWithPdf(anyString())).thenReturn("test");
        // Act
        wordToPDFConverter.convertTo(1L);

        // Assert
        verify(documentRepository).save(document);
        verify(documentConversionProgressRepository).updateDocumentConversionStatus(1L, DocumentStatus.COMPLETED.toString());
        assertEquals(FileStatus.CONVERTED.toString(), document.getStatus());
    }

    @Test
    void testConvertTo_DocumentNotFound() {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        wordToPDFConverter.convertTo(1L);

        // Assert
        verify(documentConversionProgressRepository, never()).save(any());
        verify(documentConversionProgressRepository, never()).updateDocumentConversionStatus(anyLong(), anyString());
    }

    @Test
    void testConvertTo_InvalidDocumentType() throws Exception {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setUploadedFilePath("test.txt");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentConversionProgressRepository.save(any())).thenReturn(new DocumentConversionProgress());

        // Act & Assert
        verify(documentConversionProgressRepository, never()).updateDocumentConversionStatus(anyLong(), anyString());
    }

    @Test
    void testConvertTo_ConversionFailure() throws Exception {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setUploadedFilePath("test.docx");

        DocumentConversionProgress documentConversionProgress = DocumentConversionProgress.builder().id(1L).build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentConversionProgressRepository.save(any())).thenReturn(documentConversionProgress);
        when(fileService.createDirectoryIfNotExists(any())).thenReturn(Path.of("temp"));

        // Act
        wordToPDFConverter.convertTo(1L);

        // Assert
        verify(documentConversionProgressRepository).updateDocumentConversionStatus(1L, DocumentStatus.FAILED.toString());
    }
}
