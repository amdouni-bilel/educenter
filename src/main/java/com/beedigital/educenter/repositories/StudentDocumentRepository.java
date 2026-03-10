package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {

    List<StudentDocument> findByStudentId(Long studentId);

    List<StudentDocument> findByStudentIdAndDocumentType(Long studentId, String documentType);

    void deleteByStudentId(Long studentId);
}