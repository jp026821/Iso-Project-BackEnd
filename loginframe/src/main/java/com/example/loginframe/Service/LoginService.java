package com.example.loginframe.Service;

import com.example.loginframe.Entity.User;
import com.example.loginframe.Repository.UserRepository;
import com.example.loginframe.dto.LoginRequest;
import com.example.loginframe.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpSession session) {

        LoginResponse response = new LoginResponse();


        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            response.setMessage("User Not Found");
            return response;
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            response.setMessage("Invalid password");
            return response;
        }
        session.setAttribute("userId", user.getId() );
        session.setAttribute("email", user.getEmail());
        session.setAttribute("roleName", user.getRole().getRoleName());


        if ("ADMIN".equals(user.getRole().getRoleName())) {
            session.setMaxInactiveInterval(1000);
        } else {
            session.setMaxInactiveInterval(15 * 60);
        }

        response.setMessage("Login Successfully");
        response.setRoleid(user.getRole().getId());


        return response;
    }
}
