package com.example.loginframe.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer roleid;
    private String roleName;
}
