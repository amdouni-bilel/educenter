package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.*;
import com.beedigital.educenter.entity.StudyProgram;
import com.beedigital.educenter.repositories.StudyProgramRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyProgramService {

    private final StudyProgramRepository programRepository;

    public List<StudyProgramDTO> getAll() {
        return programRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<StudyProgramDTO> getActive() {
        return programRepository.findByIsActiveTrue().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public StudyProgramDTO getById(Long id) {
        return toDTO(programRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filière non trouvée")));
    }

    public StudyProgramDTO create(CreateStudyProgramRequest req) throws Exception {
        if (programRepository.existsByCode(req.getCode().toUpperCase()))
            throw new Exception("Code filière '" + req.getCode() + "' déjà utilisé");

        StudyProgram p = StudyProgram.builder()
                .code(req.getCode().toUpperCase())
                .name(req.getName())
                .type(req.getType())
                .level(req.getLevel())
                .department(req.getDepartment())
                .academicYear(req.getAcademicYear())
                .description(req.getDescription())
                .isActive(true).build();
        return toDTO(programRepository.save(p));
    }

    public StudyProgramDTO update(Long id, CreateStudyProgramRequest req) throws Exception {
        StudyProgram p = programRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filière non trouvée"));
        p.setName(req.getName());
        p.setType(req.getType());
        p.setLevel(req.getLevel());
        p.setDepartment(req.getDepartment());
        p.setAcademicYear(req.getAcademicYear());
        p.setDescription(req.getDescription());
        return toDTO(programRepository.save(p));
    }

    public void delete(Long id) {
        if (!programRepository.existsById(id))
            throw new EntityNotFoundException("Filière non trouvée");
        programRepository.deleteById(id);
    }

    private StudyProgramDTO toDTO(StudyProgram p) {
        return StudyProgramDTO.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .type(p.getType())
                .level(p.getLevel())
                .department(p.getDepartment())
                .academicYear(p.getAcademicYear())
                .description(p.getDescription())
                .isActive(p.getIsActive()).build();
    }
}