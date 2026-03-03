package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findByTeacherId(String teacherId);

    Teacher findByEmail(String email);
}
