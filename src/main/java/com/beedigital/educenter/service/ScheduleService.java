package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;

/**
 * ScheduleService - Service pour gérer les emplois du temps
 *
 * Fonctionnalités:
 * - Créer un emploi du temps
 * - Lister les classes d'un groupe
 * - Lister les classes d'un enseignant
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    /**
     * Créer un nouvel emploi du temps
     */
    public Schedule createSchedule(Long moduleId, Long teacherId, Long groupId,
                                   String dayOfWeek, LocalTime startTime, LocalTime endTime,
                                   String room, String type) throws Exception {

        // Vérifier que le module existe (utiliser com.beedigital.educenter.entity.Module)
        com.beedigital.educenter.entity.Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new Exception("Module non trouvé"));

        // Vérifier que l'enseignant existe
        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));

        if (!(user instanceof Teacher)) {
            throw new Exception("L'utilisateur n'est pas un enseignant");
        }

        Teacher teacher = (Teacher) user;

        // Vérifier que le groupe existe
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new Exception("Groupe non trouvé"));

        // Vérifier les heures
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new Exception("L'heure de début doit être avant l'heure de fin");
        }

        // Créer l'emploi du temps
        Schedule schedule = Schedule.builder()
                .module(module)
                .teacher(teacher)
                .group(group)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .room(room)
                .type(type)  // CM, TD, TP
                .build();

        scheduleRepository.save(schedule);
        System.out.println("✅ Emploi du temps créé: " + module.getCode() + " par " + teacher.getFullName());

        return schedule;
    }

    /**
     * Obtenir tous les emplois du temps d'un groupe
     */
    public List<Schedule> getGroupSchedule(Long groupId) throws Exception {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new Exception("Groupe non trouvé"));

        return scheduleRepository.findByGroup_Id(groupId);
    }

    /**
     * Obtenir tous les emplois du temps d'un enseignant
     */
    public List<Schedule> getTeacherSchedule(Long teacherId) throws Exception {
        var user = userRepository.findById(teacherId)
                .orElseThrow(() -> new Exception("Enseignant non trouvé"));

        if (!(user instanceof Teacher)) {
            throw new Exception("L'utilisateur n'est pas un enseignant");
        }

        return scheduleRepository.findByTeacher_Id(teacherId);
    }

    /**
     * Obtenir tous les emplois du temps d'un jour spécifique
     */
    public List<Schedule> getScheduleByDay(String dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    /**
     * Supprimer un emploi du temps
     */
    public void deleteSchedule(Long scheduleId) throws Exception {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception("Emploi du temps non trouvé"));

        scheduleRepository.delete(schedule);
        System.out.println("✅ Emploi du temps supprimé: " + scheduleId);
    }
}