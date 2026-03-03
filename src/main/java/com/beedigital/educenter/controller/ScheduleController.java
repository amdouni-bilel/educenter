package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.entity.Schedule;
import com.beedigital.educenter.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * ScheduleController - Endpoints pour gérer les emplois du temps
 *
 * Endpoints:
 * - POST   /api/schedules              - Créer emploi du temps (ADMIN)
 * - GET    /api/schedules/group/{id}   - Classes d'un groupe
 * - GET    /api/schedules/teacher/{id} - Classes d'un enseignant
 * - GET    /api/schedules/day/{day}    - Classes d'un jour
 * - DELETE /api/schedules/{id}         - Supprimer (ADMIN)
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * Créer un nouvel emploi du temps
     *
     * ADMIN ONLY
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @RequestParam Long moduleId,
            @RequestParam Long teacherId,
            @RequestParam Long groupId,
            @RequestParam String dayOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam String room,
            @RequestParam(defaultValue = "CM") String type) {
        try {
            Schedule schedule = scheduleService.createSchedule(
                    moduleId, teacherId, groupId,
                    dayOfWeek, startTime, endTime,
                    room, type
            );

            return ResponseEntity.status(201)
                    .body(new ApiResponse(
                            true,
                            "✅ Emploi du temps créé",
                            schedule
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir tous les emplois du temps d'un groupe
     *
     * STUDENT peut voir son propre groupe
     * ADMIN peut voir tous les groupes
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupSchedule(@PathVariable Long groupId) {
        try {
            List<Schedule> schedules = scheduleService.getGroupSchedule(groupId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Emplois du temps du groupe",
                    schedules
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir tous les emplois du temps d'un enseignant
     *
     * TEACHER voit ses propres classes
     * ADMIN peut voir les classes de n'importe quel enseignant
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getTeacherSchedule(@PathVariable Long teacherId) {
        try {
            List<Schedule> schedules = scheduleService.getTeacherSchedule(teacherId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Classes de l'enseignant",
                    schedules
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Obtenir les emplois du temps d'un jour spécifique
     *
     * LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI
     */
    @GetMapping("/day/{day}")
    public ResponseEntity<?> getScheduleByDay(@PathVariable String day) {
        try {
            List<Schedule> schedules = scheduleService.getScheduleByDay(day);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Classes du " + day,
                    schedules
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Supprimer un emploi du temps
     *
     * ADMIN ONLY
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Emploi du temps supprimé",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }
}