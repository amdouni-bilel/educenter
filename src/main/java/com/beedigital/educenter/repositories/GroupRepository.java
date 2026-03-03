package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByCode(String code);

    Group findByLabel(String label);

    List<Group> findByProgram_Id(Long programId);
}