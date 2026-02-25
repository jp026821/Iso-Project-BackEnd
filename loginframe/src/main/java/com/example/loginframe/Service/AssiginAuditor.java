package com.example.loginframe.Service;

import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.example.loginframe.dto.AuditDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssiginAuditor {

    @Autowired
    public AuditDetailsRepository auditDetailsRepository;

    public void assignAuditor(Long auditId, AuditDetailDTO dto) {

        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setStatus(dto.getStatus());
        audit.setAdminComment(dto.getAdminComment());

        auditDetailsRepository.save(audit);
    }
}
