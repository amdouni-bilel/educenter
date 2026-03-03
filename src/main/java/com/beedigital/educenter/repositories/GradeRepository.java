package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Trouver toutes les notes d'un étudiant
    List<Grade> findByStudent_Id(Long studentId);

    // Trouver toutes les notes d'un module
    List<Grade> findByModule_Id(Long moduleId);

    // Trouver toutes les notes données par un enseignant
    List<Grade> findByTeacher_Id(Long teacherId);

    // Trouver une note spécifique
    Grade findByStudent_IdAndModule_Id(Long studentId, Long moduleId);

    // Trouver les notes non validées d'un étudiant
    List<Grade> findByStudent_IdAndIsValidatedFalse(Long studentId);

    // Trouver les notes d'un étudiant par type d'évaluation
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.evaluationType = :type")
    List<Grade> findByStudentAndType(@Param("studentId") Long studentId, @Param("type") String type);

    // Calculer la moyenne d'un étudiant
    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId")
    Double calculateAverage(@Param("studentId") Long studentId);

    // Calculer la moyenne d'un étudiant dans un module
    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId AND g.module.id = :moduleId")
    Double calculateModuleAverage(@Param("studentId") Long studentId, @Param("moduleId") Long moduleId);
}