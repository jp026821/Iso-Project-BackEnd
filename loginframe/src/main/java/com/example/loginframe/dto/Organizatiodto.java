package com.example.loginframe.dto;

import com.example.loginframe.Entity.ProfileEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organizatiodto {

    private Long organizationId;

    @NotNull(message = "Profile ID is required")
    private Long profileId;

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    private String postalCode;

}
