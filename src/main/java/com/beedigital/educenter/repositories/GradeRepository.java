package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudent_Id(Long studentId);
    List<Grade> findByTeacher_Id(Long teacherId);
    List<Grade> findByCourse_Id(Long courseId);
    List<Grade> findByTeacher_IdAndIsValidatedFalse(Long teacherId);
    List<Grade> findByStudent_IdAndEvaluationType(Long studentId, String evaluationType);
}
