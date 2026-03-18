package com.example.loginframe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private Long id;
    private String name;
    private String email;
    private String type;
    private String rating;
    private String message;
}