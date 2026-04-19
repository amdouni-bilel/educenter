package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRegistrationStatus(String status);
    long countByRole_Code(RoleEnum code);
}