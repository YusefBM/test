package com.github.dperezcabrera.test.architecture.auth.repositories;

import com.github.dperezcabrera.test.architecture.auth.entities.PermissionId;
import com.github.dperezcabrera.test.architecture.auth.entities.Permission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, PermissionId> {
    
    @Query("select p from Permission p, User u where p.id.user = u.id and u.username = :username")
    List<Permission> findByUsername(@Param("username") String username);
    
    @Query("select p from Permission p, User u where p.id.user = u.id and u.id = :userId")
    List<Permission> findByUserId(@Param("userId") long userId);
}
