package com.example.loginframe.Controller;

import com.example.loginframe.Entity.ProfileEntity;
import com.example.loginframe.Entity.ProfileOrganizationRequest;
import com.example.loginframe.Service.*;
import com.example.loginframe.dto.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthanticationController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SignupService signupService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeSignupService employeSignupService;

    @Autowired
    private IsoStandardService isoStandardService;

    @Autowired
    private AuditDetailService auditDetailService;

    @Autowired
    private AssiginAuditor assiginAuditor;

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session
    ) {
        LoginResponse response = loginService.login(request, session);
        return ResponseEntity.ok(response);
    }

    /* ================= LOGOUT ================= */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }

    /* ================= SIGNUP ================= */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        signupService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful");
    }

    /* ================= PROFILE: CREATE OR UPDATE ================= */
    @Transactional
    @PostMapping("/profile")
    public ResponseEntity<String> saveOrUpdateProfile(
            @RequestBody ProfileOrganizationRequest porequest
    ) {
        try {
            String msg = profileService.saveOrUpdateProfile(porequest); // ✅ NEW
            return ResponseEntity.ok(msg); // ✅ 200 OK for update too
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ================= PROFILE: GET BY LOGIN EMAIL ================= */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String loginEmail) {
        try {
            ProfileEntity profile = profileService.getByLoginEmail(loginEmail);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }
    }

    /* ================= USERS ================= */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable int id,
            @RequestBody UpdateUserRequest req
    ) {
        AdminUserResponse updated = userService.updateUser(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    /* ================= ADMIN ADD EMPLOYEE ================= */
    @PostMapping("/admin/add-employee")
    public ResponseEntity<String> signupEmployeeOnly(@RequestBody SignupRequest request) {
        try {
            String msg = employeSignupService.addEmployee(request);
            return ResponseEntity.ok(msg);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    /* ================= All Iso_Standards ================= */
    @GetMapping("/iso-standards")
    public ResponseEntity<List<IsoStandardDTO>> getAllIsoStandard() {

        List<IsoStandardDTO> isoList = isoStandardService.getAllIsoStandared();

        return ResponseEntity.ok(isoList);
    }

    /* ================= create Iso_Standards ================= */
    @PostMapping("/iso-standards/create")
    public ResponseEntity<String> addIsoStandard(@RequestBody IsoStandardDTO dto) {

        isoStandardService.addIsoStandard(dto);

        return ResponseEntity.ok("ISO Standard saved successfully");
    }

    /* ================= Update Iso_Standards ================= */
    @PutMapping("/iso-standards/update/{id}")
    public ResponseEntity<String> updateIsoStandard(@PathVariable Long id, @RequestBody IsoStandardDTO dto) {

        isoStandardService.updateIsoStandard(id, dto);

        return ResponseEntity.ok("ISO updated successfully");
    }

    /* ================= Delete Iso_Standards ================= */
    @DeleteMapping("/iso-standards/delete/{id}")
    public ResponseEntity<String> deleteIsoStandard(@PathVariable Long id) {

        isoStandardService.deleteIsoStandard(id);

        return ResponseEntity.ok("ISO deleted successfully");
    }

    /* ================= Create Audit Details ================= */
    @PostMapping("/audit-details")
    public ResponseEntity<String> createAudit(@RequestBody AuditDetailDTO dto) {

        auditDetailService.saveAuditDetail(dto);

        return ResponseEntity.ok("Audit created successfully");
    }

    @PutMapping("/audit-details/update/{id}")
    public ResponseEntity<String> updateAudit(@PathVariable Long id, @RequestBody AuditDetailDTO dto)
    {
        auditDetailService.updateAuditDetail(id, dto);

        return ResponseEntity.ok(" Audit Updated Successfully");
    }

    @DeleteMapping("/audit-details/delete/{id}")
    public ResponseEntity<String> deleteAudit(@PathVariable long id)
    {
        auditDetailService.deleteAudit(id);

        return ResponseEntity.ok("Audit Deleted Successfully");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AuditDetailDTO>> getpendingaudit() {

        return ResponseEntity.ok(auditDetailService.getPendingAuditsForAdmin());
    }

    @PutMapping("/pending/Assigned/{id}")
    public ResponseEntity<String> assiginAudit(@PathVariable int id, @RequestBody AuditDetailDTO auditDetailDTO)
    {
        assiginAuditor.assignAuditor((long) id,auditDetailDTO);

        return ResponseEntity.ok("Assigned successfully");
    }
}
