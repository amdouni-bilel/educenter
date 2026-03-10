package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    // email est hérité de User, findById/existsById fournis par JpaRepository
    Optional<Student> findByEmail(String email);
}