package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.CourseDTO;
import com.beedigital.educenter.dto.CreateCourseRequest;
import com.beedigital.educenter.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getAllCourses() {
        try { return ResponseEntity.ok(new ApiResponse(true, "Cours récupérés", courseService.getAllCourses())); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCourses() {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", courseService.getActiveCourses())); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR')")
    public ResponseEntity<?> countCourses() {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", courseService.countActive())); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR', 'TEACHER')")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", courseService.getCourseById(id))); }
        catch (Exception e) { return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        try { return ResponseEntity.status(201).body(new ApiResponse(true, "✅ Cours créé", courseService.createCourse(request))); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody CreateCourseRequest request) {
        try { return ResponseEntity.ok(new ApiResponse(true, "✅ Cours mis à jour", courseService.updateCourse(id, request))); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGISTRAR')")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        try { return ResponseEntity.ok(new ApiResponse(true, isActive ? "✅ Activé" : "⏸ Désactivé", courseService.toggleStatus(id, isActive))); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try { courseService.deleteCourse(id); return ResponseEntity.ok(new ApiResponse(true, "✅ Cours supprimé", null)); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }
}