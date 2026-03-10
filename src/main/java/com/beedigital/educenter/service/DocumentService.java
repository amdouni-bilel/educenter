package com.beedigital.educenter.service;

import com.beedigital.educenter.entity.StudentDocument;
import com.beedigital.educenter.repositories.StudentDocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final StudentDocumentRepository studentDocumentRepository;

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadDir;

    // Récupérer tous les documents d'un étudiant
    public List<StudentDocument> getDocumentsByStudent(Long studentId) {
        return studentDocumentRepository.findByStudentId(studentId);
    }

    // Récupérer les documents par type
    public List<StudentDocument> getDocumentsByStudentAndType(Long studentId, String documentType) {
        return studentDocumentRepository.findByStudentIdAndDocumentType(studentId, documentType);
    }

    // Récupérer un document par ID
    public StudentDocument getDocumentById(Long id) {
        return studentDocumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé avec l'ID : " + id));
    }

    // Upload et enregistrement d'un document
    public StudentDocument uploadDocument(Long studentId, String documentType, MultipartFile file) throws IOException {
        // Créer le répertoire si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Enregistrer le document en base
        StudentDocument document = new StudentDocument();
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath.toString());
        document.setFileSize(file.getSize());

        return studentDocumentRepository.save(document);
    }

    // Supprimer un document
    public void deleteDocument(Long id) throws IOException {
        StudentDocument document = getDocumentById(id);

        // Supprimer le fichier physique
        Path filePath = Paths.get(document.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        studentDocumentRepository.deleteById(id);
    }

    // Supprimer tous les documents d'un étudiant
    public void deleteAllDocumentsByStudent(Long studentId) {
        List<StudentDocument> documents = getDocumentsByStudent(studentId);
        documents.forEach(doc -> {
            try {
                Path filePath = Paths.get(doc.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                // Log l'erreur mais continue
            }
        });
        studentDocumentRepository.deleteByStudentId(studentId);
    }
}
