package com.example.loginframe.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profiledto {

    private Long profileId;

    @NotBlank(message = "Login email is required")
    @Email(message = "Please provide a valid login email")
    private String loginEmail;

    @Email(message = "Invalid notification email format")
    private String notificationEmail;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String displayName;

    private String phone;
    private String mobile;
    private String fax;

    private String organizationSize;
    private String industry;
    private String jobTitle;
    private String language;




}
