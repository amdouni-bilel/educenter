package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.StudyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {
    Optional<StudyProgram> findByCode(String code);
    boolean existsByCode(String code);
    List<StudyProgram> findByIsActiveTrue();
    List<StudyProgram> findByType(String type);
    List<StudyProgram> findByDepartment(String department);
}