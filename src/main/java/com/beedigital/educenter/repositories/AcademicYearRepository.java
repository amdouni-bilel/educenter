package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    // ✅ isActive (pas isCurrent) selon l'entité AcademicYear
    Optional<AcademicYear> findByIsActive(Boolean isActive);

    // ✅ label (pas name) selon l'entité AcademicYear
    Optional<AcademicYear> findByLabel(String label);

    boolean existsByLabel(String label);
}