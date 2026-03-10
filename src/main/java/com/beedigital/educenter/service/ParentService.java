package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.Absence;
import com.beedigital.educenter.entity.Grade;
import com.beedigital.educenter.entity.Parent;
import com.beedigital.educenter.entity.Schedule;
import com.beedigital.educenter.entity.Student;
import com.beedigital.educenter.repositories.AbsenceRepository;
import com.beedigital.educenter.repositories.GradeRepository;
import com.beedigital.educenter.repositories.ParentRepository;
import com.beedigital.educenter.repositories.ScheduleRepository;
import com.beedigital.educenter.repositories.StudentRepository;
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

    // Récupérer tous les parents
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    // Récupérer un parent par ID (= userId car héritage JPA)
    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parent non trouve avec l'ID : " + id));
    }

    // userId == id grâce à l'héritage de User
    public Parent getParentByUserId(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Parent non trouve pour l'utilisateur : " + userId));
    }

    // Récupérer les absences d'un enfant (vue parent)
    public List<Absence> getChildAbsences(Long studentId) {
        return absenceRepository.findByStudent_Id(studentId);
    }

    // Récupérer les notes d'un enfant (vue parent)
    public List<Grade> getChildGrades(Long studentId) {
        return gradeRepository.findByStudent_Id(studentId);
    }

    // Récupérer l'emploi du temps d'un enfant
    // À adapter si Student acquiert un groupId dans le futur
    public List<Schedule> getChildSchedule(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Etudiant non trouve avec l'ID : " + studentId));
        return List.of();
    }

    // Créer un parent
    public Parent createParent(Parent parent) {
        return parentRepository.save(parent);
    }

    // Mettre à jour un parent
    public Parent updateParent(Long id, Parent updatedParent) {
        Parent existing = getParentById(id);
        existing.setParentId(updatedParent.getParentId());
        existing.setRelationship(updatedParent.getRelationship());
        existing.setEmail(updatedParent.getEmail());
        return parentRepository.save(existing);
    }

    // Supprimer un parent
    public void deleteParent(Long id) {
        if (!parentRepository.existsById(id)) {
            throw new EntityNotFoundException("Parent non trouve avec l'ID : " + id);
        }
        parentRepository.deleteById(id);
    }
}