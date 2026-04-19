package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // ─── Par enseignant ───────────────────────────────────────────────────────
    List<Schedule> findByTeacher_Id(Long teacherId);

    // ─── Par cours ────────────────────────────────────────────────────────────
    List<Schedule> findByCourse_Id(Long courseId);

    // ─── Par groupe ───────────────────────────────────────────────────────────
    List<Schedule> findByGroupName(String groupName);

    // ─── Par date ─────────────────────────────────────────────────────────────
    List<Schedule> findByDate(LocalDate date);

    // ─── Par enseignant + semestre ────────────────────────────────────────────
    List<Schedule> findByTeacher_IdAndSemester(Long teacherId, String semester);

    // ─── Non annulés par enseignant ───────────────────────────────────────────
    List<Schedule> findByTeacher_IdAndIsCancelledFalse(Long teacherId);

    // ─── Par groupe + semestre ────────────────────────────────────────────────
    List<Schedule> findByGroupNameAndSemester(String groupName, String semester);
}