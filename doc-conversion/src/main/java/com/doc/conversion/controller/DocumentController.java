package com.doc.conversion.controller;

import com.doc.conversion.dto.DocumentConversionRequest;
import com.doc.conversion.dto.DocumentUploadResponse;
import com.doc.conversion.exception.DocumentNotFound;
import com.doc.conversion.exception.InvalidDocumentException;
import com.doc.conversion.message_queue.RabbitMQProducer;
import com.doc.conversion.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;


@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;
    private final RabbitMQProducer rabbitMQProducer;


    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(@RequestParam("file") MultipartFile file) throws InvalidDocumentException, IOException {
        DocumentUploadResponse documentResponse = documentService.uploadDocument(file);
        return ResponseEntity.ok(documentResponse);
    }

    @PostMapping("/convert")
    public ResponseEntity<String> convertDocument(@RequestBody @Valid DocumentConversionRequest request) {
        rabbitMQProducer.send(request);
       return ResponseEntity.status(HttpStatus.OK).build();

    }

    @GetMapping("/{documentId}/status")
    public ResponseEntity<String> getDocumentStatus(@PathVariable Long documentId) throws DocumentNotFound {
        String status  = documentService.getDocumentStatus(documentId);
        return ResponseEntity.ok(status);
    }


    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long documentId) throws MalformedURLException, InvalidDocumentException {
            Resource resource = documentService.downloadDocument(documentId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
    }
}
