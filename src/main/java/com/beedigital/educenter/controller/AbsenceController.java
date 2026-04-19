package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.*;
import com.beedigital.educenter.service.AbsenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/absences")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceService absenceService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN','TEACHER')")
    public ResponseEntity<?> getAll() {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", absenceService.getAll())); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getByStudent(@PathVariable Long id) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", absenceService.getByStudent(id))); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @GetMapping("/group/{name}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN','TEACHER')")
    public ResponseEntity<?> getByGroup(@PathVariable String name) {
        try { return ResponseEntity.ok(new ApiResponse(true, "OK", absenceService.getByGroup(name))); }
        catch (Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN','TEACHER')")
    public ResponseEntity<?> create(@RequestBody CreateAbsenceRequest req) {
        try { return ResponseEntity.status(201).body(new ApiResponse(true, "✅ Absence enregistrée", absenceService.create(req))); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @PatchMapping("/{id}/justify")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN','TEACHER')")
    public ResponseEntity<?> justify(@PathVariable Long id, @RequestBody(required = false) Map<String,String> body) {
        try {
            String justif = body != null ? body.getOrDefault("justification", "") : "";
            return ResponseEntity.ok(new ApiResponse(true, "✅ Justifiée", absenceService.justify(id, justif)));
        } catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { absenceService.delete(id); return ResponseEntity.ok(new ApiResponse(true, "✅ Supprimée", null)); }
        catch (Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null)); }
    }
}