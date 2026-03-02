package com.example.loginframe.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "documents")
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;


    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String documentCategory;
    private Integer version;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "audit_id", nullable = false)
    private AuditDetails auditDetails;

}
