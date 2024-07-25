package com.doc.conversion.service;


import com.doc.conversion.exception.InvalidDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() throws InvalidDocumentException, IOException {
        String uploadDir = "uploads";
        ReflectionTestUtils.setField(fileService, "uploadDir", uploadDir);
        MockMultipartFile file = new MockMultipartFile("file", "document.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "content".getBytes());

        File result = fileService.uploadFile(file);

        assertTrue(result.exists());
        assertTrue(result.getAbsolutePath().contains(uploadDir));
        assertTrue(result.getName().contains(".docx"));
    }



    @Test
    void testGetResource_FileNotFound() throws MalformedURLException {
        Optional<Resource> result = fileService.getResource("");

        assertTrue(result.isEmpty());
    }

    @Test
    void testReplaceDocxWithPdf() {
        String originalFileName = "document.docx";

        String result = fileService.replaceDocxWithPdf(originalFileName);

        assertEquals("document.pdf", result);
    }

    @Test
    void testGetDocxPath() {
        String filePath = "path/to/document.docx";

        Path result = fileService.getDocxPath(filePath);

        assertEquals(Paths.get(filePath), result);
    }


    @Test
    void testGetPdfOutputStream() throws IOException {
        Path path = Paths.get("uploads").resolve("document.pdf");
        Files.createDirectories(path.getParent());

        OutputStream result = fileService.getPdfOutputStream(path);

        assertNotNull(result);
    }
}

