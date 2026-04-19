
package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    List<Course> findByIsActiveTrue();
    List<Course> findBySemester(String semester);
    List<Course> findByDepartment(String department);
    long countByIsActiveTrue();
}