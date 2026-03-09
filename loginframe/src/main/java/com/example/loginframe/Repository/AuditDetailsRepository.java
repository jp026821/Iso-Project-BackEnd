package com.example.loginframe.Repository;

import com.example.loginframe.Entity.AuditDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AuditDetailsRepository extends JpaRepository<AuditDetails, Long> {

    // For Admin Pending List
    List<AuditDetails> findByStatus(String status);

    // For User Notifications
    List<AuditDetails> findByProfile_LoginEmailOrderByAuditIdDesc(String loginEmail);

    @Query("SELECT DISTINCT a FROM AuditDetails a " +
            "WHERE a.status = 'Pending' " +
            "AND (EXISTS (SELECT d FROM Documents d WHERE d.auditDetails = a AND d.status = 'Pending') " +
            "OR NOT EXISTS (SELECT d FROM Documents d WHERE d.auditDetails = a AND d.status != 'Approved'))")
    List<AuditDetails> findAuditsWithPendingDocuments();
}