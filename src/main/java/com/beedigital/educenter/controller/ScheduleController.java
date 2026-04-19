package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.CreateScheduleRequest;
import com.beedigital.educenter.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // GET /api/schedules — toutes les séances
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getAll() {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", scheduleService.getAllSchedules())); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // GET /api/schedules/date/{date}
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getByDate(@PathVariable String date) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", scheduleService.getByDate(date))); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // GET /api/schedules/group/{name}
    @GetMapping("/group/{name}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getByGroup(@PathVariable String name) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", scheduleService.getByGroup(name))); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // GET /api/schedules/teacher/{id}
    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER')")
    public ResponseEntity<?> getByTeacher(@PathVariable Long id) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", scheduleService.getByTeacher(id))); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // POST /api/schedules
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateScheduleRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "✅ Séance créée", scheduleService.createSchedule(req)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // PUT /api/schedules/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateScheduleRequest req) {
        try { return ResponseEntity.ok(new ApiResponse(true, "✅ Séance modifiée", scheduleService.updateSchedule(id, req))); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // PATCH /api/schedules/{id}/cancel
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> cancel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String reason = body.getOrDefault("reason", "");
            return ResponseEntity.ok(new ApiResponse(true, "⚠️ Séance annulée", scheduleService.cancelSchedule(id, reason)));
        } catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    // DELETE /api/schedules/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { scheduleService.deleteSchedule(id); return ResponseEntity.ok(new ApiResponse(true, "✅ Séance supprimée", null)); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }
}