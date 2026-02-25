package com.example.loginframe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDTO {

    private Long auditId;
    private String auditType;
    private LocalDate preferredDate;
    private String duration;
    private String auditLocation;
    private String status;
    private String adminComment;
    private String assignedAuditor;
    private List<String> isoStandards;
}