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
@Table(name = "profile")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(nullable = false, unique = true)
    private String loginEmail;

    private String notificationEmail;
    private String firstName;
    private String lastName;
    private String displayName;

    private String phone;
    private String mobile;
    private String fax;

    private String organizationSize;
    private String industry;
    private String jobTitle;
    private String language;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrganizationEntity organization;




}
