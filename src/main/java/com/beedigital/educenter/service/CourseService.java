package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.CourseDTO;
import com.beedigital.educenter.dto.CreateCourseRequest;
import com.beedigital.educenter.entity.Course;
import com.beedigital.educenter.repositories.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseDTO createCourse(CreateCourseRequest request) throws Exception {
        if (courseRepository.existsByCode(request.getCode()))
            throw new Exception("Un cours avec le code '" + request.getCode() + "' existe deja");
        Course course = Course.builder()
                .code(request.getCode().toUpperCase())
                .label(request.getLabel())
                .description(request.getDescription())
                .coeff(request.getCoeff())          // Double → Double OK
                .cmHours(request.getCmHours())
                .tdHours(request.getTdHours())
                .tpHours(request.getTpHours())
                .semester(request.getSemester())
                .department(request.getDepartment())
                .isActive(true)
                .build();
        return toDTO(courseRepository.save(course));
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CourseDTO> getActiveCourses() {
        return courseRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        return toDTO(courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouve : " + id)));
    }

    public CourseDTO updateCourse(Long id, CreateCourseRequest request) throws Exception {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouve : " + id));
        if (!existing.getCode().equals(request.getCode().toUpperCase())
                && courseRepository.existsByCode(request.getCode()))
            throw new Exception("Ce code est deja utilise");
        existing.setCode(request.getCode().toUpperCase());
        existing.setLabel(request.getLabel());
        existing.setDescription(request.getDescription());
        existing.setCoeff(request.getCoeff());      // Double → Double OK
        existing.setCmHours(request.getCmHours());
        existing.setTdHours(request.getTdHours());
        existing.setTpHours(request.getTpHours());
        existing.setSemester(request.getSemester());
        existing.setDepartment(request.getDepartment());
        existing.setUpdatedAt(LocalDateTime.now());
        return toDTO(courseRepository.save(existing));
    }

    public CourseDTO toggleStatus(Long id, Boolean isActive) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouve : " + id));
        course.setIsActive(isActive);
        course.setUpdatedAt(LocalDateTime.now());
        return toDTO(courseRepository.save(course));
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id))
            throw new EntityNotFoundException("Cours non trouve : " + id);
        courseRepository.deleteById(id);
    }

    public long countActive() {
        return courseRepository.countByIsActiveTrue();
    }

    private CourseDTO toDTO(Course c) {
        return CourseDTO.builder()
                .id(c.getId()).code(c.getCode()).label(c.getLabel())
                .description(c.getDescription()).coeff(c.getCoeff())
                .cmHours(c.getCmHours()).tdHours(c.getTdHours()).tpHours(c.getTpHours())
                .semester(c.getSemester()).department(c.getDepartment())
                .isActive(c.getIsActive())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build();
    }
}