package com.github.dperezcabrera.test.architecture.auth.repositories;

import com.github.dperezcabrera.test.architecture.auth.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
