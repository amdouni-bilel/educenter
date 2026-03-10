package com.beedigital.educenter.repositories;

import com.beedigital.educenter.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    // findById et existsById sont déjà fournis par JpaRepository<Parent, Long>
    // Parent hérite de User → l'id de User EST l'id de Parent
}