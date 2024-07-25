package com.doc.conversion.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentConversionRequest {

    @NotNull
    private Long documentId;

    @NotEmpty
    private String targetFormat;
}
