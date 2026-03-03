package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Module findByCode(String code);

    Module findByLabel(String label);
}