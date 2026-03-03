
package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.entity.Grade;
import com.beedigital.educenter.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GradeController - Endpoints pour gérer les notes
 *
 * Endpoints:
 * - POST   /api/grades              - Ajouter note (TEACHER)
 * - GET    /api/grades/student/{id} - Notes d'un étudiant
 * - GET    /api/grades/student/{id}/average - Moyenne d'un étudiant
 * - PUT    /api/grades/{id}/validate - Valider note (ADMIN)
 * - DELETE /api/grades/{id}         - Supprimer note (ADMIN)
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * Ajouter une note
     *
     * TEACHER ajoute une note à un étudiant
     */
    @PostMapping
    public ResponseEntity<?> addGrade(
            @RequestParam Long studentId,
            @RequestParam Long moduleId,
            @RequestParam Long teacherId,
            @RequestParam String evaluationType,  // "Contrôle", "Examen", "TD"
            @RequestParam Double value) {          // 0-20
        try {
            Grade grade = gradeService.addGrade(
                    studentId, moduleId, teacherId,
                    evaluationType, value
            );

            return ResponseEntity.status(201)
                    .body(new ApiResponse(
                            true,
                            "✅ Note enregistrée",
                            grade
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir toutes les notes d'un étudiant
     *
     * STUDENT voit ses notes
     * TEACHER/ADMIN voient les notes d'un étudiant
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentGrades(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.getStudentGrades(studentId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Notes récupérées",
                    grades
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Calculer la moyenne générale d'un étudiant
     */
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<?> getStudentAverage(@PathVariable Long studentId) {
        try {
            Double average = gradeService.calculateAverage(studentId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Moyenne calculée",
                    java.util.Map.of(
                            "studentId", studentId,
                            "average", average,
                            "status", getGradeStatus(average)
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Calculer la moyenne d'un étudiant dans un module
     */
    @GetMapping("/student/{studentId}/module/{moduleId}")
    public ResponseEntity<?> getModuleAverage(
            @PathVariable Long studentId,
            @PathVariable Long moduleId) {
        try {
            Double average = gradeService.calculateModuleAverage(studentId, moduleId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Moyenne du module",
                    java.util.Map.of(
                            "studentId", studentId,
                            "moduleId", moduleId,
                            "average", average
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Valider une note
     *
     * ADMIN valide une note
     */
    @PutMapping("/{gradeId}/validate")
    public ResponseEntity<?> validateGrade(@PathVariable Long gradeId) {
        try {
            gradeService.validateGrade(gradeId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Note validée",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir les notes non validées d'un étudiant
     *
     * ADMIN voit les notes en attente de validation
     */
    @GetMapping("/student/{studentId}/unvalidated")
    public ResponseEntity<?> getUnvalidatedGrades(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.getUnvalidatedGrades(studentId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Notes non validées",
                    grades
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Supprimer une note
     *
     * ADMIN ONLY
     */
    @DeleteMapping("/{gradeId}")
    public ResponseEntity<?> deleteGrade(@PathVariable Long gradeId) {
        try {
            gradeService.deleteGrade(gradeId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Note supprimée",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Déterminer le statut basé sur la moyenne
     */
    private String getGradeStatus(Double average) {
        if (average >= 16) return "Excellent";
        if (average >= 14) return "Très Bien";
        if (average >= 12) return "Bien";
        if (average >= 10) return "Acceptable";
        if (average >= 8) return "Passable";
        return "Échec";
    }
}
