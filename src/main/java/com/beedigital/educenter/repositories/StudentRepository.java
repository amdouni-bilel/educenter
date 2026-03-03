package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByStudentId(String studentId);

    Student findByEmail(String email);

    Student findByMatricule(String matricule);
}