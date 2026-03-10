package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.Module;
import com.beedigital.educenter.repositories.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final ModuleRepository moduleRepository;

    // Récupérer tous les cours/modules
    public List<Module> getAllCourses() {
        return moduleRepository.findAll();
    }

    // Récupérer un cours par ID
    public Module getCourseById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouve avec l'ID : " + id));
    }

    // Récupérer un cours par code
    public Module getCourseByCode(String code) {
        return moduleRepository.findByCode(code);
    }

    // Récupérer un cours par label
    public Module getCourseByLabel(String label) {
        return moduleRepository.findByLabel(label);
    }

    // Créer un cours
    public Module createCourse(Module module) {
        return moduleRepository.save(module);
    }

    // Mettre à jour un cours — utilise 'label' (pas 'name') selon l'entité Module
    public Module updateCourse(Long id, Module updatedModule) {
        Module existing = getCourseById(id);
        existing.setLabel(updatedModule.getLabel());               // ✅ label pas name
        existing.setCode(updatedModule.getCode());
        existing.setDescription(updatedModule.getDescription());
        existing.setCoefficient(updatedModule.getCoefficient());
        existing.setVolumeHoursCM(updatedModule.getVolumeHoursCM());
        existing.setVolumeHoursTD(updatedModule.getVolumeHoursTD());
        existing.setVolumeHoursTP(updatedModule.getVolumeHoursTP());
        return moduleRepository.save(existing);
    }

    // Supprimer un cours
    public void deleteCourse(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Cours non trouve avec l'ID : " + id);
        }
        moduleRepository.deleteById(id);
    }
}