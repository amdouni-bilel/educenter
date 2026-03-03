package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.entity.Absence;
import com.beedigital.educenter.entity.Student;
import com.beedigital.educenter.repositories.AbsenceRepository;
import com.beedigital.educenter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * AbsenceService - Service pour gérer les absences
 *
 * Fonctionnalités:
 * - Ajouter une absence
 * - Lister les absences d'un étudiant
 * - Justifier une absence
 * - Compter les absences
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Service
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Ajouter une absence
     */
    public void addAbsence(Long studentId, LocalDate date) throws Exception {
        // Vérifier que l'étudiant existe
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        if (!(user instanceof Student)) {
            throw new Exception("L'utilisateur n'est pas un étudiant");
        }

        Student student = (Student) user;

        // Vérifier qu'il n'existe pas déjà une absence à cette date
        Absence existing = absenceRepository.findByStudent_IdAndDate(studentId, date);
        if (existing != null) {
            throw new Exception("Une absence existe déjà pour cette date");
        }

        // Créer l'absence
        Absence absence = Absence.builder()
                .student(student)
                .date(date)
                .isJustified(false)
                .build();

        absenceRepository.save(absence);
        System.out.println("✅ Absence enregistrée pour: " + student.getEmail() + " le " + date);
    }

    /**
     * Obtenir toutes les absences d'un étudiant
     */
    public List<Absence> getStudentAbsences(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        return absenceRepository.findByStudent_Id(studentId);
    }

    /**
     * Compter les absences d'un étudiant
     */
    public Long countStudentAbsences(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        return absenceRepository.countByStudent_Id(studentId);
    }

    /**
     * Compter les absences non justifiées d'un étudiant
     */
    public Long countUnjustifiedAbsences(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        return absenceRepository.countByStudent_IdAndIsJustifiedFalse(studentId);
    }

    /**
     * Justifier une absence
     */
    public void justifyAbsence(Long absenceId, String reason) throws Exception {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new Exception("Absence non trouvée"));

        absence.setIsJustified(true);
        absence.setReason(reason != null && !reason.isEmpty() ? reason : "Justifiée");

        absenceRepository.save(absence);
        System.out.println("✅ Absence justifiée: " + absence.getId());
    }

    /**
     * Supprimer une absence
     */
    public void deleteAbsence(Long absenceId) throws Exception {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new Exception("Absence non trouvée"));

        absenceRepository.delete(absence);
        System.out.println("✅ Absence supprimée: " + absenceId);
    }
}
