package com.hostel.hostel_management.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Payment;
import com.hostel.hostel_management.entity.Student;
import com.hostel.hostel_management.repository.PaymentRepository;
import com.hostel.hostel_management.repository.StudentRepository;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public PaymentController(
            PaymentRepository paymentRepository,
            StudentRepository studentRepository) {

        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    // Get all payments
    @GetMapping
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    // Get payments of one student
    @GetMapping("/student/{studentId}")
    public List<Payment> getStudentPayments(
            @PathVariable Long studentId) {

        return paymentRepository
                .findByStudentId(studentId);
    }

    // Make demo payment
    @PostMapping("/{studentId}")
    public String makePayment(
            @PathVariable Long studentId,
            @RequestParam double amount) {

        Student student =
                studentRepository
                        .findById(studentId)
                        .orElse(null);

        if (student == null) {

            return "Student not found";
        }

        Payment payment =
                new Payment();

        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setStatus("PAID");

        payment.setTransactionId(
                "TXN" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        payment.setPaymentDate(
                LocalDate.now()
        );

        paymentRepository.save(payment);

        return "Payment successful | Transaction ID: "
                + payment.getTransactionId();
    }
}