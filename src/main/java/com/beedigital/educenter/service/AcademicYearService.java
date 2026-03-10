package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.AcademicYear;
import com.beedigital.educenter.repositories.AcademicYearRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    // Récupérer toutes les années scolaires
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    // Récupérer une année scolaire par ID
    public AcademicYear getAcademicYearById(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annee scolaire non trouvee avec l'ID : " + id));
    }

    // Récupérer l'année scolaire active
    public AcademicYear getCurrentAcademicYear() {
        return academicYearRepository.findByIsActive(true)
                .orElseThrow(() -> new EntityNotFoundException("Aucune annee scolaire active trouvee"));
    }

    // Créer une année scolaire
    public AcademicYear createAcademicYear(AcademicYear academicYear) {
        if (academicYearRepository.existsByLabel(academicYear.getLabel())) {  // ✅ label pas name
            throw new IllegalArgumentException("Une annee scolaire avec ce label existe deja.");
        }
        return academicYearRepository.save(academicYear);
    }

    // Activer une année scolaire (désactive les autres)
    @Transactional
    public AcademicYear setCurrentAcademicYear(Long id) {
        // Désactiver l'année actuellement active
        academicYearRepository.findByIsActive(true).ifPresent(current -> {
            current.setIsActive(false);           // ✅ isActive pas isCurrent
            academicYearRepository.save(current);
        });

        // Activer la nouvelle année
        AcademicYear academicYear = getAcademicYearById(id);
        academicYear.setIsActive(true);           // ✅ isActive pas setCurrent
        return academicYearRepository.save(academicYear);
    }

    // Mettre à jour une année scolaire
    public AcademicYear updateAcademicYear(Long id, AcademicYear updatedYear) {
        AcademicYear existing = getAcademicYearById(id);
        existing.setLabel(updatedYear.getLabel());           // ✅ label pas name
        existing.setStartYear(updatedYear.getStartYear());
        existing.setEndYear(updatedYear.getEndYear());
        existing.setStartDate(updatedYear.getStartDate());
        existing.setEndDate(updatedYear.getEndDate());
        return academicYearRepository.save(existing);
    }

    // Supprimer une année scolaire
    public void deleteAcademicYear(Long id) {
        if (!academicYearRepository.existsById(id)) {
            throw new EntityNotFoundException("Annee scolaire non trouvee avec l'ID : " + id);
        }
        academicYearRepository.deleteById(id);
    }
}
