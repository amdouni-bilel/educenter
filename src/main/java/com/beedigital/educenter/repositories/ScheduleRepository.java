package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Trouver tous les emplois du temps d'un groupe
    List<Schedule> findByGroup_Id(Long groupId);

    // Trouver tous les emplois du temps d'un enseignant
    List<Schedule> findByTeacher_Id(Long teacherId);

    // Trouver tous les emplois du temps d'un module
    List<Schedule> findByModule_Id(Long moduleId);

    // Trouver les emplois du temps d'un jour spécifique
    List<Schedule> findByDayOfWeek(String dayOfWeek);

    // Trouver les emplois du temps d'un groupe et d'un jour
    @Query("SELECT s FROM Schedule s WHERE s.group.id = :groupId AND s.dayOfWeek = :day")
    List<Schedule> findByGroupAndDay(@Param("groupId") Long groupId, @Param("day") String day);
}
