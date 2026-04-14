package com.example.loginframe.Service;

import com.example.loginframe.Entity.Role;
import com.example.loginframe.Entity.User;
import com.example.loginframe.Repository.RoleRepository;
import com.example.loginframe.Repository.UserRepository;
import com.example.loginframe.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class EmployeSignupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public String addEmployee(@RequestBody SignupRequest request ) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }


        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        Role role = roleRepository.findById(request.getRoleid())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        userRepository.save(user);

        return "Signup successful";
    }

    public String updateEmployee(Long id, SignupRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check if new email is already taken by a DIFFERENT user
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered by another user");
            }
            user.setEmail(request.getEmail());
        }

        // Update name fields if provided
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }

        // Update password only if provided and matches confirmPassword
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and Confirm Password do not match");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update role if provided
        if (request.getRoleid() != null) {
            Role role = roleRepository.findById(request.getRoleid())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        userRepository.save(user);

        return "Employee updated successfully";
    }
}

