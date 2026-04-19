package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.CreateGroupRequest;
import com.beedigital.educenter.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class GroupDetailController {

    private final GroupService groupService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER','STUDENT','PARENT')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue="0") int page,
                                    @RequestParam(defaultValue="5") int size) {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK", groupService.getAllGroups())); }
        catch(Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR','TEACHER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK", groupService.getGroupById(id))); }
        catch(Exception e) { return ResponseEntity.status(404).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        try { return ResponseEntity.ok(new ApiResponse(true,"OK", groupService.getActiveGroups())); }
        catch(Exception e) { return ResponseEntity.status(500).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateGroupRequest req) {
        try { return ResponseEntity.status(201).body(new ApiResponse(true,"✅ Groupe créé", groupService.createGroup(req))); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateGroupRequest req) {
        try { return ResponseEntity.ok(new ApiResponse(true,"✅ Groupe mis à jour", groupService.updateGroup(id,req))); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @PostMapping("/assign-student")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> assignStudent(@RequestBody Map<String,Object> body) {
        try {
            Long   studentId = Long.valueOf(body.get("studentId").toString());
            String groupName = body.get("groupName").toString();
            groupService.assignStudentToGroup(studentId, groupName);
            return ResponseEntity.ok(new ApiResponse(true,"✅ Étudiant affecté",null));
        } catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { groupService.deleteGroup(id); return ResponseEntity.ok(new ApiResponse(true,"✅ Groupe supprimé",null)); }
        catch(Exception e) { return ResponseEntity.status(400).body(new ApiResponse(false,e.getMessage(),null)); }
    }
}