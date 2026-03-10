package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.ExamSurveillance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamSurveillanceRepository extends JpaRepository<ExamSurveillance, Long> {

    // ✅ Seule relation dans ExamSurveillance : teacher
    List<ExamSurveillance> findByTeacher_Id(Long teacherId);

    // Par date
    List<ExamSurveillance> findByDate(LocalDate date);

    // Par salle
    List<ExamSurveillance> findByRoom(String room);

    // Confirmés ou non
    List<ExamSurveillance> findByIsConfirmed(Boolean isConfirmed);
}