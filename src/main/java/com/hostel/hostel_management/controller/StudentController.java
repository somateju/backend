package com.hostel.hostel_management.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Room;
import com.hostel.hostel_management.entity.RoomAllocation;
import com.hostel.hostel_management.entity.Student;
import com.hostel.hostel_management.repository.PaymentRepository;
import com.hostel.hostel_management.repository.PreferenceRepository;
import com.hostel.hostel_management.repository.RoomAllocationRepository;
import com.hostel.hostel_management.repository.RoomRepository;
import com.hostel.hostel_management.repository.StudentRepository;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final PreferenceRepository preferenceRepository;
    private final PaymentRepository paymentRepository;
    private final RoomAllocationRepository allocationRepository;
    private final RoomRepository roomRepository;

    public StudentController(
            StudentRepository studentRepository,
            PreferenceRepository preferenceRepository,
            PaymentRepository paymentRepository,
            RoomAllocationRepository allocationRepository,
            RoomRepository roomRepository) {

        this.studentRepository = studentRepository;
        this.preferenceRepository = preferenceRepository;
        this.paymentRepository = paymentRepository;
        this.allocationRepository = allocationRepository;
        this.roomRepository = roomRepository;
    }

    // ==========================================
    // REGISTER STUDENT
    // ==========================================

    @PostMapping("/register")
    public String registerStudent(
            @RequestBody Student student) {

        if (student.getEmail() == null ||
                student.getEmail().trim().isEmpty()) {

            return "Email is required";
        }

        boolean emailExists =
                studentRepository
                        .findByEmailIgnoreCase(
                                student.getEmail().trim()
                        )
                        .isPresent();

        if (emailExists) {

            return "Email already registered";
        }

        student.setEmail(
                student.getEmail().trim()
        );

        studentRepository.save(student);

        return "Registration successful";
    }

    // ==========================================
    // GET ALL STUDENTS
    // ==========================================

    @GetMapping
    public List<Student> getAllStudents() {

        return studentRepository.findAll();
    }

    // ==========================================
    // ADD STUDENT
    // ==========================================

    @PostMapping
    public Student addStudent(
            @RequestBody Student student) {

        return studentRepository.save(student);
    }

    // ==========================================
    // DELETE STUDENT
    // ==========================================

    @DeleteMapping("/{studentId}")
    @Transactional
    public String deleteStudent(
            @PathVariable Long studentId) {

        Student student =
                studentRepository
                        .findById(studentId)
                        .orElse(null);

        if (student == null) {

            return "Student not found";
        }

        // ------------------------------------------
        // 1. Delete room allocation
        // ------------------------------------------

        List<RoomAllocation> allocations =
                allocationRepository.findAll()
                        .stream()
                        .filter(allocation ->
                                allocation.getStudent() != null &&
                                allocation.getStudent()
                                        .getId()
                                        .equals(studentId)
                        )
                        .toList();

        for (RoomAllocation allocation :
                allocations) {

            Room room =
                    allocation.getRoom();

            if (room != null &&
                    room.getOccupied() > 0) {

                room.setOccupied(
                        room.getOccupied() - 1
                );

                roomRepository.save(room);
            }

            allocationRepository.delete(
                    allocation
            );
        }

        // ------------------------------------------
        // 2. Delete preferences
        // ------------------------------------------

        preferenceRepository
                .findByStudentId(studentId)
                .ifPresent(
                        preferenceRepository::delete
                );

        // ------------------------------------------
        // 3. Delete payments
        // ------------------------------------------

        paymentRepository
                .findByStudentId(studentId)
                .forEach(
                        paymentRepository::delete
                );

        // ------------------------------------------
        // 4. Delete student
        // ------------------------------------------

        studentRepository.delete(student);

        return "Student deleted successfully";
    }
}