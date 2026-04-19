package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final AbsenceRepository absenceRepository;
    private final GradeRepository gradeRepository;

    // ─── Dashboard SUPER ADMIN / ADMIN ────────────────────────────────────────
    public Map<String, Object> getAdminDashboard() {
        List<User> allUsers = userRepository.findAll();

        long totalStudents        = allUsers.stream().filter(u -> u instanceof Student).count();
        long totalTeachers        = allUsers.stream().filter(u -> u instanceof Teacher).count();
        long totalParents         = allUsers.stream().filter(u -> u instanceof Parent).count();
        long pendingRegistrations = allUsers.stream()
                .filter(u -> "PENDING".equals(u.getRegistrationStatus())).count();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalUsers",           allUsers.size());
        dashboard.put("totalStudents",        totalStudents);
        dashboard.put("totalTeachers",        totalTeachers);
        dashboard.put("totalParents",         totalParents);
        dashboard.put("pendingRegistrations", pendingRegistrations);
        dashboard.put("recentUsers",          allUsers.stream().limit(5).toList());
        return dashboard;
    }

    // ─── Dashboard TEACHER ────────────────────────────────────────────────────
    public Map<String, Object> getTeacherDashboard(Long teacherId) throws Exception {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));
        if (!(user instanceof Teacher teacher))
            throw new Exception("L'utilisateur n'est pas un enseignant");

        List<Grade> gradesToValidate = gradeRepository.findByTeacher_Id(teacherId)
                .stream().filter(g -> !g.getIsValidated()).toList();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("teacherName",           teacher.getFullName());
        dashboard.put("gradesToValidate",      gradesToValidate);
        dashboard.put("gradesToValidateCount", gradesToValidate.size());
        return dashboard;
    }

    // ─── Dashboard STUDENT ────────────────────────────────────────────────────
    public Map<String, Object> getStudentDashboard(Long studentId) throws Exception {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new Exception("Étudiant non trouvé"));
        if (!(user instanceof Student student))
            throw new Exception("L'utilisateur n'est pas un etudiant");

        List<Grade>   myGrades    = gradeRepository.findByStudent_Id(studentId);
        List<Absence> myAbsences  = absenceRepository.findByStudent_Id(studentId);
        Long          unjustified = absenceRepository.countByStudent_IdAndIsJustifiedFalse(studentId);
        Double        average     = calculateAverage(myGrades);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("studentName",         student.getFullName());
        dashboard.put("myGrades",            myGrades);
        dashboard.put("average",             average);
        dashboard.put("myAbsences",          myAbsences);
        dashboard.put("totalAbsences",       myAbsences.size());
        dashboard.put("unjustifiedAbsences", unjustified);
        return dashboard;
    }

    // ─── Dashboard PARENT ─────────────────────────────────────────────────────
    public Map<String, Object> getParentDashboard(Long parentId, Long childId) throws Exception {
        User user = userRepository.findById(parentId)
                .orElseThrow(() -> new Exception("Parent non trouvé"));
        if (!(user instanceof Parent parent))
            throw new Exception("L'utilisateur n'est pas un parent");

        User childUser = userRepository.findById(childId)
                .orElseThrow(() -> new Exception("Enfant non trouvé"));
        if (!(childUser instanceof Student child))
            throw new Exception("L'enfant n'est pas un etudiant");

        List<Grade>   childGrades   = gradeRepository.findByStudent_Id(childId);
        List<Absence> childAbsences = absenceRepository.findByStudent_Id(childId);
        Long          unjustified   = absenceRepository.countByStudent_IdAndIsJustifiedFalse(childId);
        Double        average       = calculateAverage(childGrades);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("parentName",          parent.getFullName());
        dashboard.put("childName",           child.getFullName());
        dashboard.put("childGrades",         childGrades);
        dashboard.put("average",             average);
        dashboard.put("childAbsences",       childAbsences);
        dashboard.put("totalAbsences",       childAbsences.size());
        dashboard.put("unjustifiedAbsences", unjustified);
        return dashboard;
    }

    // ─── Calculer la moyenne (interne) ────────────────────────────────────────
    private Double calculateAverage(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;
        double totalWeighted = 0.0;
        double totalCoeff    = 0.0;
        for (Grade g : grades) {
            double coeff = (g.getCoefficient() != null) ? g.getCoefficient() : 1.0;
            totalWeighted += g.getValue() * coeff;
            totalCoeff    += coeff;
        }
        return totalCoeff > 0 ? Math.round((totalWeighted / totalCoeff) * 100.0) / 100.0 : 0.0;
    }
}