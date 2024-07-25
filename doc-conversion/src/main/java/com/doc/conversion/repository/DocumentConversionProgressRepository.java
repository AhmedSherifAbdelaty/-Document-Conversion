package com.doc.conversion.repository;

import com.doc.conversion.entity.DocumentConversionProgress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DocumentConversionProgressRepository extends JpaRepository<DocumentConversionProgress, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE DocumentConversionProgress d SET d.status = :status  WHERE d.id = :id ")
    int updateDocumentConversionStatus(@Param("id") Long id, @Param("status") String status);

    Optional<DocumentConversionProgress> findFirstByDocumentIdOrderByCreatedAtDesc(Long documentId);


}