package com.javachat.repository;

import com.javachat.model.Role;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
    @Query("select r from Role r where r.is_deleted = false")
    List<Role> findAllNotDeleted();
    Optional<Role> findByName(String name);
}