package com.example.loginframe.Service;

import com.example.loginframe.Entity.Role;
import com.example.loginframe.Entity.User;
import com.example.loginframe.Repository.RoleRepository;
import com.example.loginframe.Repository.UserRepository;
import com.example.loginframe.dto.AdminUserResponse;
import com.example.loginframe.dto.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new AdminUserResponse(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        (u.getRole() != null ? u.getRole().getId() : null),
                        (u.getRole() != null ? u.getRole().getRoleName() : "Not Assigned")
                ))
                .toList();
    }

    public AdminUserResponse updateUser(int id, UpdateUserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());

        if (req.getRoleid() != null) {
            Role role = roleRepository.findById(req.getRoleid())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            u.setRole(role);
        }

        User saved = userRepository.save(u);

        return new AdminUserResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole() != null ? saved.getRole().getId() : null,
                saved.getRole() != null ? saved.getRole().getRoleName() : "Not Assigned"
        );
    }


    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
