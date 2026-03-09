package com.example.loginframe.Service;

import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Entity.IsoStandard;
import com.example.loginframe.Entity.ProfileEntity;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.example.loginframe.Repository.IsoStandardRepository;
import com.example.loginframe.Repository.ProfileRepository;
import com.example.loginframe.dto.AuditDetailDTO;
import com.example.loginframe.dto.DocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuditDetailService {

    @Autowired
    private AuditDetailsRepository auditDetailsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private IsoStandardRepository isoStandardRepository;

    // ===================== CREATE =====================
    public AuditDetails saveAuditDetail(AuditDetailDTO dto) {

        AuditDetails audit = new AuditDetails();

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());


        String status = (dto.getStatus() == null || dto.getStatus().trim().isEmpty())
                ? "pending"
                : dto.getStatus().trim().toLowerCase();
        audit.setStatus(status);

        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        // ✅ attach profile using loginEmail
        String email = (dto.getLoginEmail() == null) ? "" : dto.getLoginEmail().trim();
        if (email.isEmpty()) {
            throw new RuntimeException("loginEmail is required");
        }

        ProfileEntity profile = profileRepository.findByLoginEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found for: " + email));

        // ✅ attach ISO standards
        Set<IsoStandard> isoSet = new HashSet<>();
        if (dto.getIsoStandards() != null) {
            for (String isoCode : dto.getIsoStandards()) {
                if (isoCode == null) continue;
                String code = isoCode.trim();
                if (code.isEmpty()) continue;


                IsoStandard iso = isoStandardRepository.findByIsoCode(isoCode)
                        .orElseThrow(() -> new RuntimeException("ISO not found"));

                isoSet.add(iso);
            }
        }

        audit.setIsoStandards(isoSet);
        audit.setProfile(profile);


        return auditDetailsRepository.save(audit);

    }

    // ===================== UPDATE =====================
    public AuditDetails updateAuditDetail(long id, AuditDetailDTO dto) {

        AuditDetails audit = auditDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            audit.setStatus(dto.getStatus().trim().toLowerCase());
        }

        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        // ✅ profile update by loginEmail
        String email = (dto.getLoginEmail() == null) ? "" : dto.getLoginEmail().trim();
        if (!email.isEmpty()) {
            ProfileEntity profile = profileRepository
                    .findByLoginEmail(email)
                    .orElseThrow(() -> new RuntimeException("Profile not found for: " + email));
            audit.setProfile(profile);
        }

        // ✅ also update ISO list when updating
        if (dto.getIsoStandards() != null) {
            Set<IsoStandard> isoSet = new HashSet<>();
            for (String isoCode : dto.getIsoStandards()) {
                if (isoCode == null) continue;
                String code = isoCode.trim();
                if (code.isEmpty()) continue;

                IsoStandard iso = isoStandardRepository.findByIsoCode(code)
                        .orElseThrow(() -> new RuntimeException("ISO not found: " + code));
                isoSet.add(iso);
            }
            audit.setIsoStandards(isoSet);
        }

        return auditDetailsRepository.save(audit);
    }

    // ===================== ADMIN: PENDING AUDITS =====================
    public List<AuditDetailDTO> getPendingAuditsForAdmin() {
        return auditDetailsRepository.findAuditsWithPendingDocuments()
                .stream()
                .map(audit -> toDto(audit, true))
                .toList();
    }

    // ===================== USER: NOTIFICATIONS =====================
    public List<AuditDetailDTO> getUserNotifications(String loginEmail) {

        if (loginEmail == null || loginEmail.trim().isEmpty()) return List.of();

        List<AuditDetails> audits = auditDetailsRepository
                .findByProfile_LoginEmailOrderByAuditIdDesc(loginEmail.trim());

        if (audits == null) return List.of();

        List<AuditDetailDTO> dtoList = new ArrayList<>();
        for (AuditDetails audit : audits) {
            dtoList.add(toDto(audit, true));
        }
        return dtoList;
    }

    // ===================== HELPER: ENTITY -> DTO =====================
    private AuditDetailDTO toDto(AuditDetails audit, boolean includeLoginEmail) {

        AuditDetailDTO dto = new AuditDetailDTO();

        dto.setAuditId(audit.getAuditId());
        dto.setAuditType(audit.getAuditType());
        dto.setPreferredDate(audit.getPreferredDate());
        dto.setDuration(audit.getDuration());
        dto.setAuditLocation(audit.getAuditLocation());
        dto.setScope(audit.getScope());
        dto.setNotes(audit.getNotes());
        dto.setStatus(audit.getStatus());
        dto.setAssignedAuditor(audit.getAssignedAuditor());
        dto.setAdminComment(audit.getAdminComment());

        if (includeLoginEmail && audit.getProfile() != null) {
            dto.setLoginEmail(audit.getProfile().getLoginEmail());
        }

        // ✅ IMPORTANT:
        // your frontend expects isoStandards like ["ISO9001","ISO27001"]
        // but you were returning "ISO9001  ISO Name"
        // returning only isoCode is better for consistency
        List<String> isoCodes = (audit.getIsoStandards() == null)
                ? List.of()
                : audit.getIsoStandards().stream()
                .map(IsoStandard::getIsoCode)
                .toList();

        dto.setIsoStandards(isoCodes);

        // ✅ Add documents to dto
        List<DocumentDTO> docList = (audit.getDocuments() == null)
                ? new ArrayList<>()
                : audit.getDocuments().stream()
                .map(doc -> new DocumentDTO(
                        doc.getId(),
                        doc.getFileName(),
                        doc.getDocType(),
                        doc.getStatus(),
                        doc.getAdminComment()
                ))
                .toList();
        dto.setDocuments(docList);

        return dto;

    }
}