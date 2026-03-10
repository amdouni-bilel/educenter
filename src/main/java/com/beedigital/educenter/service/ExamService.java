package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.ExamSurveillance;
import com.beedigital.educenter.repositories.ExamSurveillanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamSurveillanceRepository examSurveillanceRepository;

    // Récupérer tous les examens
    public List<ExamSurveillance> getAllExams() {
        return examSurveillanceRepository.findAll();
    }

    // Récupérer un examen par ID
    public ExamSurveillance getExamById(Long id) {
        return examSurveillanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Examen non trouvé avec l'ID : " + id));
    }

    // ✅ Seule relation disponible : teacher
    public List<ExamSurveillance> getExamsByTeacher(Long teacherId) {
        return examSurveillanceRepository.findByTeacher_Id(teacherId);
    }

    // Par date
    public List<ExamSurveillance> getExamsByDate(LocalDate date) {
        return examSurveillanceRepository.findByDate(date);
    }

    // Par salle
    public List<ExamSurveillance> getExamsByRoom(String room) {
        return examSurveillanceRepository.findByRoom(room);
    }

    // Examens confirmés ou non
    public List<ExamSurveillance> getExamsByConfirmation(Boolean isConfirmed) {
        return examSurveillanceRepository.findByIsConfirmed(isConfirmed);
    }

    // Créer un examen
    public ExamSurveillance createExam(ExamSurveillance exam) {
        return examSurveillanceRepository.save(exam);
    }

    // Mettre à jour un examen
    public ExamSurveillance updateExam(Long id, ExamSurveillance updatedExam) {
        ExamSurveillance existing = getExamById(id);
        existing.setExamName(updatedExam.getExamName());
        existing.setRoom(updatedExam.getRoom());
        existing.setDate(updatedExam.getDate());
        existing.setStartTime(updatedExam.getStartTime());
        existing.setEndTime(updatedExam.getEndTime());
        existing.setIsConfirmed(updatedExam.getIsConfirmed());
        return examSurveillanceRepository.save(existing);
    }

    // Confirmer un examen
    public ExamSurveillance confirmExam(Long id) {
        ExamSurveillance exam = getExamById(id);
        exam.setIsConfirmed(true);
        return examSurveillanceRepository.save(exam);
    }

    // Supprimer un examen
    public void deleteExam(Long id) {
        if (!examSurveillanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Examen non trouvé avec l'ID : " + id);
        }
        examSurveillanceRepository.deleteById(id);
    }
}

