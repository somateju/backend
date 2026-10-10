package com.hostel.hostel_management.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Admin;
import com.hostel.hostel_management.entity.Student;
import com.hostel.hostel_management.repository.AdminRepository;
import com.hostel.hostel_management.repository.StudentRepository;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    public LoginController(
            StudentRepository studentRepository,
            AdminRepository adminRepository) {

        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    @PostMapping
    public Map<String, String> login(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        Map<String, String> response =
                new HashMap<>();

        if (email == null || password == null) {

            response.put(
                    "message",
                    "Email and password are required"
            );

            return response;
        }

        email = email.trim();
        password = password.trim();

        // Check students
        for (Student student :
                studentRepository.findAll()) {

            if (student.getEmail() != null &&
                    student.getPassword() != null &&
                    student.getEmail()
                            .trim()
                            .equalsIgnoreCase(email) &&
                    student.getPassword()
                            .equals(password)) {

                response.put(
                        "message",
                        "Login successful"
                );

                response.put(
                        "role",
                        "STUDENT"
                );

                response.put(
                        "name",
                        student.getName()
                );

                response.put(
                        "email",
                        student.getEmail()
                );

                return response;
            }
        }

        // Check admin
        for (Admin admin :
                adminRepository.findAll()) {

            if (admin.getEmail() != null &&
                    admin.getPassword() != null &&
                    admin.getEmail()
                            .trim()
                            .equalsIgnoreCase(email) &&
                    admin.getPassword()
                            .equals(password)) {

                response.put(
                        "message",
                        "Login successful"
                );

                response.put(
                        "role",
                        "ADMIN"
                );

                response.put(
                        "name",
                        admin.getName()
                );

                response.put(
                        "email",
                        admin.getEmail()
                );

                return response;
            }
        }

        response.put(
                "message",
                "Invalid email or password"
        );

        return response;
    }
}