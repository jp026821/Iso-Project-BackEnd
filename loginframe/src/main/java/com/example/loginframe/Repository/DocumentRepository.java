package com.example.loginframe.Repository;

import com.example.loginframe.Entity.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentRepository extends JpaRepository<Documents,Long> {

    // It will find the documents by auditdetails id
    List<Documents> findByAuditDetails_AuditId(Long auditId);

    // It will delete the documents by auditdetails id
    void deleteByAuditDetails_AuditId(Long auditId);

    List<Documents> findByAuditDetails_AuditIdAndStatus(Long auditId, String rejected);
}
