package com.example.loginframe.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_details")
public class AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private String auditType;
    private LocalDate preferredDate;
    private String duration;
    private String auditLocation;

    @Column(length = 2000)
    private String scope;

    @Column(length = 2000)
    private String notes;

    private String status = "pending";
    private String assignedAuditor;
    private String adminComment;


    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;


    @ManyToMany
    @JoinTable(
            name = "audit_iso_mapping",
            joinColumns = @JoinColumn(name = "audit_id"),
            inverseJoinColumns = @JoinColumn(name = "iso_id")
    )
    private Set<IsoStandard> isoStandards;


}