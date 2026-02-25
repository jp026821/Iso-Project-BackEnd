package com.example.loginframe.Repository;

import com.example.loginframe.Entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity,Integer> {
}
