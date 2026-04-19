package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Recherche par groupName (String)
    List<Student> findByGroupName(String groupName);

    // Recherche paginée par groupName
    Page<Student> findByGroupName(String groupName, Pageable pageable);

    // Comptage par groupName
    long countByGroupName(String groupName);

    // Étudiants actifs
    List<Student> findByIsActiveTrue();

    // Comptage total actifs
    long countByIsActiveTrue();
}