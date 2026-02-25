package com.example.loginframe.Service;

import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Entity.IsoStandard;
import com.example.loginframe.Entity.ProfileEntity;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.example.loginframe.Repository.IsoStandardRepository;
import com.example.loginframe.Repository.ProfileRepository;
import com.example.loginframe.dto.AuditDetailDTO;
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
    public void saveAuditDetail(AuditDetailDTO dto) {

        AuditDetails audit = new AuditDetails();

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());

        // ✅ keep status consistent
        // If DTO is null/blank, default to "Pending"
        String status = (dto.getStatus() == null || dto.getStatus().trim().isEmpty())
                ? "Pending"
                : dto.getStatus().trim();
        audit.setStatus(status);

        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        ProfileEntity profile = profileRepository.findByLoginEmail(dto.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Set<IsoStandard> isoSet = new HashSet<>();
        if (dto.getIsoStandards() != null) {
            for (String isoCode : dto.getIsoStandards()) {
                if (isoCode == null) continue;
                String code = isoCode.trim();
                if (code.isEmpty()) continue;

                // ✅ no casting needed
                IsoStandard iso = isoStandardRepository.findByIsoCode(isoCode)
                        .orElseThrow(() -> new RuntimeException("ISO not found"));

                isoSet.add(iso);
            }
        }

        audit.setIsoStandards(isoSet);
        audit.setProfile(profile);

        auditDetailsRepository.save(audit);
    }

    // ===================== UPDATE =====================
    public void updateAuditDetail(long id, AuditDetailDTO dto) {

        AuditDetails audit = auditDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            audit.setStatus(dto.getStatus().trim());
        }

        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        ProfileEntity profile = profileRepository
                .findByLoginEmail(dto.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        audit.setProfile(profile);

        // ✅ also update ISO list when updating
        if (dto.getIsoStandards() != null) {
            Set<IsoStandard> isoSet = new HashSet<>();
            for (String isoCode : dto.getIsoStandards()) {
                if (isoCode == null) continue;
                String code = isoCode.trim();
                if (code.isEmpty()) continue;

                IsoStandard iso = isoStandardRepository.findByIsoCode(isoCode)
                        .orElseThrow(() -> new RuntimeException("ISO not found: " + isoCode));
                isoSet.add(iso);
            }
            audit.setIsoStandards(isoSet);
        }

        auditDetailsRepository.save(audit);
    }

    // ===================== DELETE =====================
    public void deleteAudit(long id) {
        if (!auditDetailsRepository.existsById(id)) {
            throw new RuntimeException("Audit not found");
        }
        auditDetailsRepository.deleteById(id);
    }

    // ===================== ADMIN: PENDING AUDITS =====================
    public List<AuditDetailDTO> getPendingAuditsForAdmin() {

        // ✅ Your entity default is "pending", but your admin code checks "Pending"
        // Use one consistent value. Here we try both to be safe.
        List<AuditDetails> audits = new ArrayList<>();
        try {
            audits = auditDetailsRepository.findByStatus("Pending");
        } catch (Exception ignored) {}

        if (audits == null || audits.isEmpty()) {
            try {
                audits = auditDetailsRepository.findByStatus("pending");
            } catch (Exception ignored) {}
        }

        if (audits == null) audits = List.of();

        List<AuditDetailDTO> dtoList = new ArrayList<>();

        for (AuditDetails audit : audits) {
            dtoList.add(toDto(audit, true));
        }

        return dtoList;
    }

    // ===================== USER: NOTIFICATIONS =====================
    // ✅ Call this from controller:
    // GET /api/audit-details/user?loginEmail=...
    public List<AuditDetailDTO> getUserNotifications(String loginEmail) {

        if (loginEmail == null || loginEmail.trim().isEmpty()) return List.of();

        List<AuditDetails> audits = auditDetailsRepository
                .findByProfile_LoginEmailOrderByAuditIdDesc(loginEmail.trim());

        if (audits == null) return List.of();

        List<AuditDetailDTO> dtoList = new ArrayList<>();
        for (AuditDetails audit : audits) {
            // include loginEmail in response
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

        List<String> isoCodes = (audit.getIsoStandards() == null)
                ? List.of()
                : audit.getIsoStandards().stream()
                .map(iso -> iso.getIsoCode() + "  " + iso.getIsoName())
                .toList();

        dto.setIsoStandards(isoCodes);

        return dto;
    }
}