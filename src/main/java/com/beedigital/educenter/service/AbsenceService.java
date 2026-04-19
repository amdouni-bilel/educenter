package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.AbsenceDTO;
import com.beedigital.educenter.dto.CreateAbsenceRequest;
import com.beedigital.educenter.entity.Absence;
import com.beedigital.educenter.repositories.AbsenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final AbsenceRepository absenceRepository;

    public List<AbsenceDTO> getAll() {
        return absenceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AbsenceDTO> getByStudent(Long studentId) {
        return absenceRepository.findByStudentId(studentId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AbsenceDTO> getByGroup(String groupName) {
        return absenceRepository.findByGroupName(groupName).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AbsenceDTO create(CreateAbsenceRequest req) {
        // Résoudre les alias
        String course = req.getCourseLabel() != null ? req.getCourseLabel()
                : req.getCourse() != null ? req.getCourse()
                : req.getCourseName() != null ? req.getCourseName() : "—";

        Boolean justified = req.getIsJustified() != null ? req.getIsJustified()
                : req.getJustified() != null ? req.getJustified() : false;

        Absence a = Absence.builder()
                .studentId(req.getStudentId())
                .studentName(req.getStudentName())
                .groupName(req.getGroupName())
                .courseLabel(course)
                .date(req.getDate())
                .type(req.getType() != null ? req.getType() : "CM")
                .isJustified(justified)
                .justification(req.getJustification())
                .build();
        return toDTO(absenceRepository.save(a));
    }

    public AbsenceDTO justify(Long id, String justification) {
        Absence a = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Absence non trouvée : " + id));
        a.setIsJustified(true);
        if (justification != null) a.setJustification(justification);
        return toDTO(absenceRepository.save(a));
    }

    public void delete(Long id) {
        if (!absenceRepository.existsById(id))
            throw new EntityNotFoundException("Absence non trouvée : " + id);
        absenceRepository.deleteById(id);
    }

    private AbsenceDTO toDTO(Absence a) {
        return AbsenceDTO.builder()
                .id(a.getId())
                .studentId(a.getStudentId())
                .studentName(a.getStudentName())
                .groupName(a.getGroupName())
                .courseLabel(a.getCourseLabel())
                .date(a.getDate())
                .type(a.getType())
                .isJustified(a.getIsJustified())
                .justification(a.getJustification())
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .build();
    }
}