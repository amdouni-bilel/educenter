package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Role;
import com.beedigital.educenter.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(RoleEnum code);
}