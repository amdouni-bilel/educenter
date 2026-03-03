package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * GradeService - Service pour gérer les notes
 *
 * Fonctionnalités:
 * - Ajouter une note
 * - Lister les notes d'un étudiant
 * - Calculer la moyenne
 * - Valider les notes
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    /**
     * Ajouter une note
     */
    public Grade addGrade(Long studentId, Long moduleId, Long teacherId,
                          String evaluationType, Double value) throws Exception {

        // Vérifier que l'étudiant existe
        var studentUser = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        if (!(studentUser instanceof Student)) {
            throw new Exception("L'utilisateur n'est pas un étudiant");
        }

        Student student = (Student) studentUser;

        // Vérifier que le module existe (utiliser com.beedigital.educenter.entity.Module)
        com.beedigital.educenter.entity.Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new Exception("Module non trouvé"));

        // Vérifier que l'enseignant existe
        var teacherUser = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));

        if (!(teacherUser instanceof Teacher)) {
            throw new Exception("L'utilisateur n'est pas un enseignant");
        }

        Teacher teacher = (Teacher) teacherUser;

        // Vérifier la note (0-20)
        if (value < 0 || value > 20) {
            throw new Exception("La note doit être entre 0 et 20");
        }

        // Créer la note
        Grade grade = Grade.builder()
                .student(student)
                .module(module)
                .teacher(teacher)
                .evaluationType(evaluationType)  // "Contrôle", "Examen", "TD"
                .value(value)
                .coefficient(1.0)
                .isValidated(false)
                .build();

        gradeRepository.save(grade);
        System.out.println("✅ Note enregistrée: " + student.getFullName() + " - " + module.getCode() + " = " + value);

        return grade;
    }

    /**
     * Obtenir toutes les notes d'un étudiant
     */
    public List<Grade> getStudentGrades(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        return gradeRepository.findByStudent_Id(studentId);
    }

    /**
     * Obtenir les notes d'un étudiant dans un module
     */
    public Double getModuleGrade(Long studentId, Long moduleId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        Grade grade = gradeRepository.findByStudent_IdAndModule_Id(studentId, moduleId);

        return grade != null ? grade.getValue() : null;
    }

    /**
     * Calculer la moyenne générale d'un étudiant
     */
    public Double calculateAverage(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        Double average = gradeRepository.calculateAverage(studentId);
        return average != null ? average : 0.0;
    }

    /**
     * Calculer la moyenne d'un étudiant dans un module
     */
    public Double calculateModuleAverage(Long studentId, Long moduleId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        Double average = gradeRepository.calculateModuleAverage(studentId, moduleId);
        return average != null ? average : 0.0;
    }

    /**
     * Valider une note
     */
    public void validateGrade(Long gradeId) throws Exception {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new Exception("Note non trouvée"));

        grade.setIsValidated(true);
        gradeRepository.save(grade);

        System.out.println("✅ Note validée: " + grade.getValue() + " pour " + grade.getStudent().getFullName());
    }

    /**
     * Obtenir les notes non validées d'un étudiant
     */
    public List<Grade> getUnvalidatedGrades(Long studentId) throws Exception {
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        return gradeRepository.findByStudent_IdAndIsValidatedFalse(studentId);
    }

    /**
     * Obtenir les notes d'un enseignant
     */
    public List<Grade> getTeacherGrades(Long teacherId) throws Exception {
        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));

        return gradeRepository.findByTeacher_Id(teacherId);
    }

    /**
     * Supprimer une note
     */
    public void deleteGrade(Long gradeId) throws Exception {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new Exception("Note non trouvée"));

        gradeRepository.delete(grade);
        System.out.println("✅ Note supprimée: " + gradeId);
    }
}