package com.doc.conversion.service;

import com.doc.conversion.exception.InvalidDocumentException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Optional;

import static com.doc.conversion.utils.Utils.UPLOADED_FILE_DIR;

@Service
@Slf4j
public class FileService {

     @Value("${file.upload.dir}")
    private String uploadDir;

    public File uploadFile(MultipartFile file) throws InvalidDocumentException, IOException {
        log.info("Uploading file {}", file.getOriginalFilename());
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String uploadedFileName = getNewFileNameWithDate(originalFilename);
        String uploadedFilePath = System.getProperty("user.dir") + File.separator + uploadDir;
        Path uploadPath = createDirectoryIfNotExists(uploadedFilePath);

        // Resolve the new file path
        Path filePath = uploadPath.resolve(uploadedFileName);

        // Copy the file to the new location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toFile() ;
    }


    protected Optional<Resource> getResource(String uploadedFilePath) throws MalformedURLException {
        if (!StringUtils.isEmpty(uploadedFilePath)) {
            Path fileStorageLocation = Paths.get(UPLOADED_FILE_DIR).toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(uploadedFilePath).normalize();
            return Optional.of(new UrlResource(filePath.toUri()));
        } return Optional.empty() ;
    }


    private String getNewFileNameWithDate(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name.");
        }
        return FilenameUtils.getBaseName(fileName)+ " - " + System.currentTimeMillis() + "." + FilenameUtils.getExtension(fileName);
    }



    private void validateFile(MultipartFile file) throws InvalidDocumentException {
        if (file.isEmpty()) {
            throw new InvalidDocumentException("File is empty");
        }
    }

    public Path createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        return directory;
    }

    public Path getDocxPath(String filePath) {
        return Paths.get(filePath);
    }

    public InputStream getDocxInputStream(Path docxPath) throws FileNotFoundException {
        return new FileInputStream(docxPath.toFile());
    }

    public OutputStream getPdfOutputStream(Path pdfPath) throws FileNotFoundException {
        return new FileOutputStream(pdfPath.toFile());
    }

    public String replaceDocxWithPdf(String originalFileName) {
        return originalFileName.replace(".docx", ".pdf");
    }










}
