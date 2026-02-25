package com.example.loginframe.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "iso_standard")
public class IsoStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long isoId;

    @Column(unique = true, nullable = false)
    private String isoCode;

    private String isoName;

    @ManyToMany(mappedBy = "isoStandards")
    private Set<AuditDetails> audits;


}