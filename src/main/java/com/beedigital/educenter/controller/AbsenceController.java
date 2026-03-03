package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.entity.Absence;
import com.beedigital.educenter.service.AbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AbsenceController - Endpoints pour gérer les absences
 *
 * Endpoints:
 * - POST   /api/absences              - Ajouter absence (TEACHER)
 * - GET    /api/absences/student/{id} - Absences d'un étudiant
 * - PUT    /api/absences/{id}/justify - Justifier absence (ADMIN)
 * - DELETE /api/absences/{id}         - Supprimer absence (ADMIN)
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;

    /**
     * Ajouter une absence
     *
     * TEACHER peut enregistrer l'absence d'un étudiant
     */
    @PostMapping
    public ResponseEntity<?> addAbsence(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            absenceService.addAbsence(studentId, date);
            return ResponseEntity.status(201)
                    .body(new ApiResponse(true, "✅ Absence enregistrée", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir toutes les absences d'un étudiant
     *
     * STUDENT voit ses absences
     * TEACHER/ADMIN voient les absences d'un étudiant
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentAbsences(@PathVariable Long studentId) {
        try {
            List<Absence> absences = absenceService.getStudentAbsences(studentId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Absences récupérées",
                    absences
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Compter les absences d'un étudiant
     */
    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<?> countAbsences(@PathVariable Long studentId) {
        try {
            Long count = absenceService.countStudentAbsences(studentId);
            Long unjustified = absenceService.countUnjustifiedAbsences(studentId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Comptage des absences",
                    java.util.Map.of(
                            "totalAbsences", count,
                            "unjustifiedAbsences", unjustified
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Justifier une absence
     *
     * ADMIN peut justifier une absence
     */
    @PutMapping("/{absenceId}/justify")
    public ResponseEntity<?> justifyAbsence(
            @PathVariable Long absenceId,
            @RequestParam(required = false) String reason) {
        try {
            absenceService.justifyAbsence(absenceId, reason);
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Absence justifiée",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Supprimer une absence
     *
     * ADMIN ONLY
     */
    @DeleteMapping("/{absenceId}")
    public ResponseEntity<?> deleteAbsence(@PathVariable Long absenceId) {
        try {
            absenceService.deleteAbsence(absenceId);
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Absence supprimée",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }
}