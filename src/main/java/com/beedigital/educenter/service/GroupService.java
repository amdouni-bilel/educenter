package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.*;
import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository   groupRepository;
    private final StudentRepository studentRepository;
    private final UserRepository    userRepository;

    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<GroupDTO> getActiveGroups() {
        return groupRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public GroupDTO getGroupById(Long id) {
        return toDTO(groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé : " + id)));
    }

    public GroupDTO createGroup(CreateGroupRequest req) throws Exception {
        if (groupRepository.existsByName(req.getName().toUpperCase()))
            throw new Exception("Groupe '" + req.getName() + "' déjà existant");
        Group g = Group.builder()
                .name(req.getName().toUpperCase())
                .level(req.getLevel())
                .session(req.getSession())
                .department(req.getDepartment())
                .academicYear(req.getAcademicYear())
                .maxStudents(req.getMaxStudents())
                .isActive(true)
                .build();
        return toDTO(groupRepository.save(g));
    }

    public GroupDTO updateGroup(Long id, CreateGroupRequest req) throws Exception {
        Group g = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé : " + id));
        if (!g.getName().equals(req.getName().toUpperCase())
                && groupRepository.existsByName(req.getName()))
            throw new Exception("Ce nom est déjà utilisé");
        g.setName(req.getName().toUpperCase());
        g.setLevel(req.getLevel());
        g.setSession(req.getSession());
        g.setDepartment(req.getDepartment());
        g.setAcademicYear(req.getAcademicYear());
        g.setMaxStudents(req.getMaxStudents());
        return toDTO(groupRepository.save(g));
    }

    public GroupDTO toggleStatus(Long id, Boolean isActive) {
        Group g = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé : " + id));
        g.setIsActive(isActive);
        return toDTO(groupRepository.save(g));
    }

    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id))
            throw new EntityNotFoundException("Groupe non trouvé : " + id);
        groupRepository.deleteById(id);
    }

    public void assignStudentToGroup(Long studentId, String groupName) throws Exception {
        User u = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
        if (!(u instanceof Student student))
            throw new Exception("L'utilisateur n'est pas un étudiant");
        groupRepository.findByName(groupName.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Groupe '" + groupName + "' non trouvé"));
        student.setGroupName(groupName.toUpperCase());
        userRepository.save(student);
    }

    public List<UserDTO> getStudentsByGroup(String groupName) {
        return studentRepository.findByGroupName(groupName.toUpperCase()).stream()
                .map(s -> UserDTO.builder()
                        .id(s.getId()).firstName(s.getFirstName())
                        .lastName(s.getLastName()).email(s.getEmail())
                        .isActive(s.getIsActive()).build())
                .collect(Collectors.toList());
    }

    private GroupDTO toDTO(Group g) {
        long count = studentRepository.countByGroupName(g.getName());
        return GroupDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .level(g.getLevel())
                .session(g.getSession())
                .department(g.getDepartment())
                .academicYear(g.getAcademicYear())
                .maxStudents(g.getMaxStudents())
                .studentCount((int) count)
                .isActive(g.getIsActive())
                .createdAt(g.getCreatedAt() != null ? g.getCreatedAt().toString() : null)
                .build();
    }
}