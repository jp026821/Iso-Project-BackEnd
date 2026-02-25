package com.example.loginframe.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "organization")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;

    @OneToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private ProfileEntity profile;

    private String company;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;




}

