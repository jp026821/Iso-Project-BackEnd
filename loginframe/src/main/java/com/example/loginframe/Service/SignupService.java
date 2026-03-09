package com.example.loginframe.Service;

import com.example.loginframe.Entity.Role;
import com.example.loginframe.Entity.User;
import com.example.loginframe.Repository.RoleRepository;
import com.example.loginframe.Repository.UserRepository;
import com.example.loginframe.dto.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public SignupService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public String signup(SignupRequest request) {


        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Password and Confirm Password do not match";
        }


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return  "Email already registered";
        }

        Role role = roleRepository.findById(request.getRoleid())
                .orElseThrow(() -> new RuntimeException("Role not found"));

       User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);

        return "Signup successful";
    }

}
