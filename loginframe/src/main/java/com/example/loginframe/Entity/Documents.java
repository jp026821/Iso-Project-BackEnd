package com.example.loginframe.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

        private String fileName;
        private String docType;

        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] data;

        private String status = "Pending";   // Pending, Approved, Rejected
        private String adminComment;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "audit_id", nullable = false)
        private AuditDetails auditDetails;


}
