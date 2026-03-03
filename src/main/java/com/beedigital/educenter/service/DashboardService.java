package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * DashboardService - Service pour les dashboards
 *
 * Crée les données pour chaque dashboard:
 * - Admin Dashboard
 * - Teacher Dashboard
 * - Student Dashboard
 * - Parent Dashboard
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GradeService gradeService;

    /**
     * Dashboard ADMIN
     */
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Compter les utilisateurs
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long totalStudents = allUsers.stream().filter(u -> u instanceof Student).count();
        long totalTeachers = allUsers.stream().filter(u -> u instanceof Teacher).count();
        long pendingRegistrations = allUsers.stream()
                .filter(u -> "PENDING".equals(u.getRegistrationStatus())).count();

        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalStudents", totalStudents);
        dashboard.put("totalTeachers", totalTeachers);
        dashboard.put("pendingRegistrations", pendingRegistrations);
        dashboard.put("recentUsers", allUsers.stream().limit(5).toList());

        return dashboard;
    }

    /**
     * Dashboard TEACHER
     */
    public Map<String, Object> getTeacherDashboard(Long teacherId) throws Exception {
        Map<String, Object> dashboard = new HashMap<>();

        // Vérifier que c'est un enseignant
        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));

        if (!(user instanceof Teacher)) {
            throw new Exception("L'utilisateur n'est pas un enseignant");
        }

        Teacher teacher = (Teacher) user;

        // Obtenir les classes de l'enseignant
        List<Schedule> myClasses = scheduleRepository.findByTeacher_Id(teacherId);

        // Obtenir les notes à valider
        List<Grade> gradesToValidate = gradeRepository.findByTeacher_Id(teacherId).stream()
                .filter(g -> !g.getIsValidated())
                .toList();

        dashboard.put("teacherName", teacher.getFullName());
        dashboard.put("myClasses", myClasses);
        dashboard.put("gradesToValidate", gradesToValidate);
        dashboard.put("gradesToValidateCount", gradesToValidate.size());

        return dashboard;
    }

    /**
     * Dashboard STUDENT
     */
    public Map<String, Object> getStudentDashboard(Long studentId) throws Exception {
        Map<String, Object> dashboard = new HashMap<>();

        // Vérifier que c'est un étudiant
        var user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));

        if (!(user instanceof Student)) {
            throw new Exception("L'utilisateur n'est pas un étudiant");
        }

        Student student = (Student) user;

        // Obtenir les notes
        List<Grade> myGrades = gradeRepository.findByStudent_Id(studentId);

        // Calculer la moyenne
        Double average = gradeService.calculateAverage(studentId);

        // Obtenir les absences
        List<Absence> myAbsences = absenceRepository.findByStudent_Id(studentId);

        // Compter absences non justifiées
        Long unjustifiedAbsences = absenceRepository.countByStudent_IdAndIsJustifiedFalse(studentId);

        dashboard.put("studentName", student.getFullName());
        dashboard.put("myGrades", myGrades);
        dashboard.put("average", average);
        dashboard.put("myAbsences", myAbsences);
        dashboard.put("totalAbsences", myAbsences.size());
        dashboard.put("unjustifiedAbsences", unjustifiedAbsences);

        return dashboard;
    }

    /**
     * Dashboard PARENT
     */
    public Map<String, Object> getParentDashboard(Long parentId, Long childId) throws Exception {
        Map<String, Object> dashboard = new HashMap<>();

        // Vérifier que c'est un parent
        var user = userRepository.findById(parentId)
                .orElseThrow(() -> new Exception("Parent non trouvé"));

        if (!(user instanceof Parent)) {
            throw new Exception("L'utilisateur n'est pas un parent");
        }

        Parent parent = (Parent) user;

        // Vérifier que l'enfant existe
        var childUser = userRepository.findById(childId)
                .orElseThrow(() -> new Exception("Enfant non trouvé"));

        if (!(childUser instanceof Student)) {
            throw new Exception("L'enfant n'est pas un étudiant");
        }

        Student child = (Student) childUser;

        // Obtenir les notes de l'enfant
        List<Grade> childGrades = gradeRepository.findByStudent_Id(childId);

        // Calculer la moyenne
        Double average = gradeService.calculateAverage(childId);

        // Obtenir les absences
        List<Absence> childAbsences = absenceRepository.findByStudent_Id(childId);

        // Compter absences non justifiées
        Long unjustifiedAbsences = absenceRepository.countByStudent_IdAndIsJustifiedFalse(childId);

        dashboard.put("parentName", parent.getFullName());
        dashboard.put("childName", child.getFullName());
        dashboard.put("childGrades", childGrades);
        dashboard.put("average", average);
        dashboard.put("childAbsences", childAbsences);
        dashboard.put("totalAbsences", childAbsences.size());
        dashboard.put("unjustifiedAbsences", unjustifiedAbsences);

        return dashboard;
    }
}