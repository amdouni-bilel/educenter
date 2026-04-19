package com.beedigital.educenter.repositories;
import com.beedigital.educenter.entity.Group;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
    boolean existsByName(String name);
    Page<Group> findAll(Pageable pageable);
    List<Group> findByIsActiveTrue();
    List<Group> findBySession(String session);             // JOUR ou SOIR
    List<Group> findByStudyProgram_Id(Long programId);
    List<Group> findByAcademicYear(String academicYear);
}