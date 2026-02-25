package com.example.loginframe.Repository;

import com.example.loginframe.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role,Integer> {

    @Override
    Optional<Role> findById(Integer integer);
}
