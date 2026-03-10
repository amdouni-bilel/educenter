package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    // Champs disponibles dans Program : code, label, level, diploma, duration, description

    Optional<Program> findByCode(String code);

    boolean existsByCode(String code);

    List<Program> findByLevel(String level);

    List<Program> findByDiploma(String diploma);
}