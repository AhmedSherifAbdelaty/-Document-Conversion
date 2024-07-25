package com.doc.conversion.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@Entity
@Data
public class DocumentConversionProgress {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private String conversionType;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
