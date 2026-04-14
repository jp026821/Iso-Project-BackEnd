package com.example.loginframe.Service;

import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Entity.AuditUpdate;
import com.example.loginframe.Entity.Documents;
import com.example.loginframe.Entity.NotificationEntity;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.example.loginframe.Repository.AuditUpdateRepository;
import com.example.loginframe.Repository.NotificationRepository;
import com.example.loginframe.dto.AuditRemarkRequest;
import com.example.loginframe.dto.AuditStatusUpdateRequest;
import com.example.loginframe.dto.AuditorAuditDTO;
import com.example.loginframe.dto.DocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditorService {

    @Autowired
    private AuditDetailsRepository auditDetailsRepository;

    @Autowired
    private AuditUpdateRepository auditUpdateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public List<AuditorAuditDTO> getAssignedAudits(String auditorEmail) {
        List<AuditDetails> audits = auditDetailsRepository.findByAssignedAuditorOrderByAuditIdDesc(auditorEmail);
        List<AuditorAuditDTO> result = new ArrayList<>();

        for (AuditDetails audit : audits) {
            result.add(toDto(audit, false));
        }

        return result;
    }

    public AuditorAuditDTO getAuditDetails(Long auditId, String auditorEmail) {
        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        validateAuditor(audit, auditorEmail);

        return toDto(audit, true);
    }

    public List<AuditUpdate> getAuditUpdates(Long auditId, String auditorEmail) {
        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        validateAuditor(audit, auditorEmail);

        return auditUpdateRepository.findByAuditIdOrderByCreatedAtDesc(auditId);
    }

    public String addRemark(Long auditId, AuditRemarkRequest request) {
        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        validateAuditor(audit, request.getAuditorEmail());

        audit.setAuditorComment(request.getRemark());
        auditDetailsRepository.save(audit);

        auditUpdateRepository.save(
                AuditUpdate.builder()
                        .auditId(auditId)
                        .updatedBy(request.getAuditorEmail())
                        .role("AUDITOR")
                        .message(request.getRemark())
                        .statusAfterUpdate(audit.getStatus())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        notifyAdmin(auditId, "Auditor Remark Added",
                "Auditor added a remark for audit ID " + auditId);

        return "Remark saved successfully";
    }

    public String updateAuditStatus(Long auditId, AuditStatusUpdateRequest request) {
        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        validateAuditor(audit, request.getAuditorEmail());

        audit.setStatus(request.getStatus());

        // ✅ Stamp updateTime when status becomes "completed"
        if ("completed".equalsIgnoreCase(request.getStatus()) && audit.getUpdateTime() == null) {
            audit.setUpdateTime(LocalDate.now());
        }

        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            audit.setAuditorComment(request.getMessage());
        }

        auditDetailsRepository.save(audit);

        auditUpdateRepository.save(
                AuditUpdate.builder()
                        .auditId(auditId)
                        .updatedBy(request.getAuditorEmail())
                        .role("AUDITOR")
                        .message(request.getMessage())
                        .statusAfterUpdate(request.getStatus())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        notifyAdmin(
                auditId,
                "Audit Status Updated",
                "Audit ID " + auditId + " updated to status: " + request.getStatus()
        );

        String clientEmail = null;
        if (audit.getProfile() != null) {
            clientEmail = audit.getProfile().getLoginEmail();
        }

        if (clientEmail != null && !clientEmail.isBlank()) {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userEmail(clientEmail)
                            .auditId(auditId)
                            .title("Audit Status Updated")
                            .message("Your audit status is now: " + request.getStatus())
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }

        return "Audit status updated successfully";
    }

    private void validateAuditor(AuditDetails audit, String auditorEmail) {
        if (audit.getAssignedAuditor() == null || auditorEmail == null ||
                !audit.getAssignedAuditor().equalsIgnoreCase(auditorEmail)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    private void notifyAdmin(Long auditId, String title, String message) {
        notificationRepository.save(
                NotificationEntity.builder()
                        .userEmail("admin@gmail.com")
                        .auditId(auditId)
                        .title(title)
                        .message(message)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    private AuditorAuditDTO toDto(AuditDetails audit, boolean includeDocuments) {
        List<String> isoList = new ArrayList<>();

        if (audit.getIsoStandards() != null) {
            audit.getIsoStandards().forEach(iso -> {
                String code = iso.getIsoCode() != null ? iso.getIsoCode() : "";
                String name = iso.getIsoName() != null ? iso.getIsoName() : "";
                isoList.add((code + " " + name).trim());
            });
        }

        List<DocumentDTO> documentDTOs = new ArrayList<>();

        if (includeDocuments && audit.getDocuments() != null) {
            for (Documents doc : audit.getDocuments()) {
                DocumentDTO dto = new DocumentDTO(
                        doc.getId(),
                        doc.getFileName(),
                        doc.getDocType(),
                        doc.getStatus(),
                        doc.getAdminComment()
                );
                documentDTOs.add(dto);
            }
        }

        String clientEmail = null;
        if (audit.getProfile() != null) {
            clientEmail = audit.getProfile().getLoginEmail();
        }

        return AuditorAuditDTO.builder()
                .auditId(audit.getAuditId())
                .auditType(audit.getAuditType())
                .preferredDate(audit.getPreferredDate() != null ? audit.getPreferredDate().toString() : "")
                .duration(audit.getDuration())
                .auditLocation(audit.getAuditLocation())
                .scope(audit.getScope())
                .notes(audit.getNotes())
                .status(audit.getStatus())
                .assignedAuditor(audit.getAssignedAuditor())
                .auditorComment(audit.getAuditorComment())
                .adminComment(audit.getAdminComment())
                .loginEmail(clientEmail)
                .isoStandards(isoList)
                .documents(documentDTOs)
                .build();
    }
}