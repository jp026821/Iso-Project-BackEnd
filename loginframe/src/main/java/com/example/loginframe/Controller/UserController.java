package com.example.loginframe.Controller;

import com.example.loginframe.Service.EmployeSignupService;
import com.example.loginframe.Service.UserService;
import com.example.loginframe.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmployeSignupService employeSignupService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PostMapping("/admin/add-employee")
    public ResponseEntity<String> signupEmployeeOnly(@RequestBody SignupRequest request) {
        try {
            return ResponseEntity.ok(employeSignupService.addEmployee(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    // ── Update existing employee by ID ──
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable Long id,
            @RequestBody SignupRequest request) {
        String result = employeSignupService.updateEmployee(id, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/products")
    public ResponseEntity<List<String>> getProducts() {
        return ResponseEntity.ok(List.of(
                "ISO Certification",
                "Internal Audit",
                "External Audit"
        ));
    }
}
