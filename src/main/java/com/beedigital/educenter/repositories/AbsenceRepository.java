package com.beedigital.educenter.repositories;
import com.beedigital.educenter.entity.Student;
import com.beedigital.educenter.entity.Absence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // Compter absences d'un étudiant
    Long countByStudent_Id(Long studentId);

    // Trouver absences d'un étudiant
    List<Absence> findByStudent_Id(Long studentId);

    // Trouver absences d'un étudiant à une date spécifique
    Absence findByStudent_IdAndDate(Long studentId, LocalDate date);

    // Trouver absences non justifiées
    List<Absence> findByStudent_IdAndIsJustifiedFalse(Long studentId);

    // Compter absences non justifiées
    Long countByStudent_IdAndIsJustifiedFalse(Long studentId);
}
