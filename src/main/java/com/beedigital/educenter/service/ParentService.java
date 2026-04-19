package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.Absence;
import com.beedigital.educenter.entity.Grade;
import com.beedigital.educenter.entity.Parent;
import com.beedigital.educenter.entity.Schedule;
import com.beedigital.educenter.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final AbsenceRepository absenceRepository;
    private final GradeRepository gradeRepository;
    private final ScheduleRepository scheduleRepository;

    // ─── Lister tous les parents ──────────────────────────────────────────────
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    // ─── Récupérer par ID ─────────────────────────────────────────────────────
    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parent non trouvé avec l'ID : " + id));
    }

    // ─── Récupérer par userId ─────────────────────────────────────────────────
    public Parent getParentByUserId(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Parent non trouvé pour l'utilisateur : " + userId));
    }

    // ─── Absences d'un enfant ─────────────────────────────────────────────────
    public List<Absence> getChildAbsences(Long studentId) {
        return absenceRepository.findByStudent_Id(studentId);
    }

    // ─── Notes d'un enfant ────────────────────────────────────────────────────
    public List<Grade> getChildGrades(Long studentId) {
        return gradeRepository.findByStudent_Id(studentId);
    }

    // ─── Emploi du temps d'un enfant ──────────────────────────────────────────
    public List<Schedule> getChildSchedule(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Etudiant non trouvé avec l'ID : " + studentId));
        // À compléter quand Student aura un groupName
        return List.of();
    }

    // ─── Créer un parent ──────────────────────────────────────────────────────
    public Parent createParent(Parent parent) {
        return parentRepository.save(parent);
    }

    // ─── Modifier un parent ───────────────────────────────────────────────────
    public Parent updateParent(Long id, Parent updatedParent) {
        Parent existing = getParentById(id);
        existing.setParentId(updatedParent.getParentId());
        existing.setRelationship(updatedParent.getRelationship());
        existing.setEmail(updatedParent.getEmail());
        return parentRepository.save(existing);
    }

    // ─── Supprimer un parent ──────────────────────────────────────────────────
    public void deleteParent(Long id) {
        if (!parentRepository.existsById(id))
            throw new EntityNotFoundException("Parent non trouvé avec l'ID : " + id);
        parentRepository.deleteById(id);
    }
}