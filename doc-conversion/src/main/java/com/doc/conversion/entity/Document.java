package com.doc.conversion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@Entity
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;

    private String uploadedFilePath;
    private String uploadedFileName;

    private String convertedFilename;
    private String convertedFilePath;

    private Long fileSize;
    private String contentType;
    private String status;

}
