package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // ── Nouvelle API (studentId direct) ──────────────────────────────────────
    List<Absence> findByStudentId(Long studentId);
    long countByStudentIdAndIsJustifiedFalse(Long studentId);

    // ── Ancienne API (pour DashboardService et ParentService) ─────────────────
    default List<Absence> findByStudent_Id(Long studentId) {
        return findByStudentId(studentId);
    }
    default long countByStudent_IdAndIsJustifiedFalse(Long studentId) {
        return countByStudentIdAndIsJustifiedFalse(studentId);
    }

    List<Absence> findByGroupName(String groupName);
    List<Absence> findByIsJustifiedFalse();
}