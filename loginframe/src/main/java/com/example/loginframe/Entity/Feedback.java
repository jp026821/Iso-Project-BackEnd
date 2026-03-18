package com.example.loginframe.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    @Size(max = 150)
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Feedback type is required")
    @Size(max = 100)
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @NotBlank(message = "Rating is required")
    @Size(max = 50)
    @Column(name = "rating", nullable = false, length = 50)
    private String rating;

    @NotBlank(message = "Message is required")
    @Size(max = 2000)
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
}