package com.beedigital.educenter.controller;
import com.beedigital.educenter.dto.*;
import com.beedigital.educenter.service.StudyProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/programs")
@CrossOrigin(origins = "*", maxAge = 3600) @RequiredArgsConstructor
public class StudyProgramController {
    private final StudyProgramService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getAll() {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK",service.getAll())); }
        catch(Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK",service.getActive())); }
        catch(Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK",service.getById(id))); }
        catch(Exception e) { return ResponseEntity.status(404).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateStudyProgramRequest req) {
        try { return ResponseEntity.status(201).body(new ApiResponse(true,"✅ Filière créée",service.create(req))); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateStudyProgramRequest req) {
        try { return ResponseEntity.ok(new ApiResponse(true,"✅ Filière mise à jour",service.update(id,req))); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.delete(id); return ResponseEntity.ok(new ApiResponse(true,"✅ Filière supprimée",null)); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }
}