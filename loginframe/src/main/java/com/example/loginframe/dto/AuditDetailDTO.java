package com.example.loginframe.dto;

import com.example.loginframe.Entity.ProfileEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDetailDTO {

    private Long auditId;
    private String auditType;
    private LocalDate preferredDate;
    private String duration;
    private String auditLocation;
    private String scope;
    private String notes;
    private String status = "Pending";
    private String assignedAuditor;
    private String adminComment;
    private String loginEmail;

    private List<String> isoStandards;

}
