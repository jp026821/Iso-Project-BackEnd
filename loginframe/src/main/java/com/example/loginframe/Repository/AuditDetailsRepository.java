package com.example.loginframe.Repository;

import com.example.loginframe.Entity.AuditDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditDetailsRepository extends JpaRepository<AuditDetails, Long> {

    // ✅ For Admin Pending List
    List<AuditDetails> findByStatus(String status);

    // ✅ For User Notifications
    List<AuditDetails> findByProfile_LoginEmailOrderByAuditIdDesc(String loginEmail);
}