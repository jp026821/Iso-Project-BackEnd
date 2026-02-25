package com.example.loginframe.Repository;

import com.example.loginframe.Entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByLoginEmail(String loginEmail);
}
