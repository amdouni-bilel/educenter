package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.CreateScheduleRequest;
import com.beedigital.educenter.dto.ScheduleDTO;
import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseRepository   courseRepository;
    private final UserRepository     userRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ─── Créer une séance ─────────────────────────────────────────────────────
    public ScheduleDTO createSchedule(CreateScheduleRequest req) throws Exception {
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé"));

        User teacher = userRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé"));
        if (!(teacher instanceof Teacher))
            throw new Exception("L'utilisateur n'est pas un enseignant");

        LocalTime start = LocalTime.parse(req.getStartTime(), TIME_FMT);
        LocalTime end   = LocalTime.parse(req.getEndTime(),   TIME_FMT);
        if (!end.isAfter(start))
            throw new Exception("L'heure de fin doit être après l'heure de début");

        // JOUR si début < 18h, SOIR sinon
        String session = start.isBefore(LocalTime.of(18, 0)) ? "JOUR" : "SOIR";

        Schedule schedule = Schedule.builder()
                .course(course)
                .teacher((Teacher) teacher)
                .groupName(req.getGroupName().toUpperCase())
                .date(LocalDate.parse(req.getDate(), DATE_FMT))
                .startTime(start)
                .endTime(end)
                .room(req.getRoom())
                .type(req.getType())
                .semester(req.getSemester())
                .isCancelled(false)
                .build();

        return toDTO(scheduleRepository.save(schedule), session);
    }

    // ─── Toutes les séances ───────────────────────────────────────────────────
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(s -> toDTO(s, calcSession(s)))
                .collect(Collectors.toList());
    }

    // ─── Par date ─────────────────────────────────────────────────────────────
    public List<ScheduleDTO> getByDate(String date) {
        LocalDate d = LocalDate.parse(date, DATE_FMT);
        return scheduleRepository.findByDate(d).stream()
                .map(s -> toDTO(s, calcSession(s)))
                .collect(Collectors.toList());
    }

    // ─── Par groupe ───────────────────────────────────────────────────────────
    public List<ScheduleDTO> getByGroup(String groupName) {
        return scheduleRepository.findByGroupName(groupName.toUpperCase()).stream()
                .map(s -> toDTO(s, calcSession(s)))
                .collect(Collectors.toList());
    }

    // ─── Par enseignant ───────────────────────────────────────────────────────
    public List<ScheduleDTO> getByTeacher(Long teacherId) {
        return scheduleRepository.findByTeacher_Id(teacherId).stream()
                .map(s -> toDTO(s, calcSession(s)))
                .collect(Collectors.toList());
    }

    // ─── Annuler une séance ───────────────────────────────────────────────────
    public ScheduleDTO cancelSchedule(Long id, String reason) {
        Schedule s = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée"));
        s.setIsCancelled(true);
        s.setCancelReason(reason);
        return toDTO(scheduleRepository.save(s), calcSession(s));
    }

    // ─── Modifier une séance ──────────────────────────────────────────────────
    public ScheduleDTO updateSchedule(Long id, CreateScheduleRequest req) throws Exception {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Séance non trouvée"));

        LocalTime start = LocalTime.parse(req.getStartTime(), TIME_FMT);
        LocalTime end   = LocalTime.parse(req.getEndTime(),   TIME_FMT);
        if (!end.isAfter(start))
            throw new Exception("L'heure de fin doit être après l'heure de début");

        existing.setDate(LocalDate.parse(req.getDate(), DATE_FMT));
        existing.setStartTime(start);
        existing.setEndTime(end);
        existing.setRoom(req.getRoom());
        existing.setType(req.getType());
        existing.setGroupName(req.getGroupName().toUpperCase());
        return toDTO(scheduleRepository.save(existing), calcSession(existing));
    }

    // ─── Supprimer ────────────────────────────────────────────────────────────
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id))
            throw new EntityNotFoundException("Séance non trouvée");
        scheduleRepository.deleteById(id);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String calcSession(Schedule s) {
        if (s.getStartTime() == null) return "JOUR";
        return s.getStartTime().isBefore(LocalTime.of(18, 0)) ? "JOUR" : "SOIR";
    }

    private ScheduleDTO toDTO(Schedule s, String session) {
        String teacherName = "";
        if (s.getTeacher() != null)
            teacherName = s.getTeacher().getFirstName() + " " + s.getTeacher().getLastName();

        return ScheduleDTO.builder()
                .id(s.getId())
                .courseCode(s.getCourse() != null ? s.getCourse().getCode()  : "")
                .courseLabel(s.getCourse() != null ? s.getCourse().getLabel() : "")
                .teacherName(teacherName)
                .teacherId(s.getTeacher() != null ? s.getTeacher().getId() : null)
                .groupName(s.getGroupName())
                .date(s.getDate() != null ? s.getDate().format(DATE_FMT) : "")
                .startTime(s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "")
                .endTime(s.getEndTime()   != null ? s.getEndTime().format(TIME_FMT)   : "")
                .room(s.getRoom())
                .type(s.getType())
                .session(session)
                .isCancelled(s.getIsCancelled())
                .cancelReason(s.getCancelReason())
                .build();
    }
}