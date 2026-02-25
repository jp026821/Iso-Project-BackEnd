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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuditDetailService {

    @Autowired
    private AuditDetailsRepository auditDetailsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private IsoStandardRepository isoStandardRepository;

    public void saveAuditDetail(AuditDetailDTO dto)
    {

        AuditDetails audit = new AuditDetails();

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());
        audit.setStatus(dto.getStatus());
        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        ProfileEntity profile = profileRepository.findByLoginEmail(dto.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Set<IsoStandard> isoSet = new HashSet<>();

        if(dto.getIsoStandards() != null){

            for(String isoCode : dto.getIsoStandards()){

                IsoStandard iso = (IsoStandard) isoStandardRepository.findByIsoCode(isoCode)
                        .orElseThrow(() -> new RuntimeException("ISO not found"));

                isoSet.add(iso);
            }
        }

        audit.setIsoStandards(isoSet);

        audit.setProfile(profile);

        auditDetailsRepository.save(audit);
    }

    public void updateAuditDetail(long id, AuditDetailDTO dto)
    {
        AuditDetails audit = auditDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ISO not found"));

        audit.setAuditType(dto.getAuditType());
        audit.setPreferredDate(dto.getPreferredDate());
        audit.setDuration(dto.getDuration());
        audit.setAuditLocation(dto.getAuditLocation());
        audit.setScope(dto.getScope());
        audit.setNotes(dto.getNotes());
        audit.setStatus(dto.getStatus());
        audit.setAssignedAuditor(dto.getAssignedAuditor());
        audit.setAdminComment(dto.getAdminComment());

        ProfileEntity profile = profileRepository
                .findByLoginEmail(dto.getLoginEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        audit.setProfile(profile);

        auditDetailsRepository.save(audit);
    }

    public void deleteAudit(long id)
    {
        if(!auditDetailsRepository.existsById(id)) {
            throw new RuntimeException("ISO not found");
        }

        auditDetailsRepository.deleteById(id);
    }

    public List<AuditDetailDTO> getPendingAuditsForAdmin() {

        List<AuditDetails> audits =
                auditDetailsRepository.findByStatus("Pending");

        List<AuditDetailDTO> dtoList = new ArrayList<>();

        for (AuditDetails audit : audits) {

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
            dto.setLoginEmail(audit.getProfile().getLoginEmail());

            List<String> isoCodes = audit.getIsoStandards().stream().map(iso -> iso.getIsoCode()+"  " +
                    iso.getIsoName()).toList();

            dto.setIsoStandards(isoCodes);

            dtoList.add(dto);
        }

        return dtoList;
    }
}
