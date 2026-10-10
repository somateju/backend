package com.hostel.hostel_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    Optional<Payment> findTopByStudentIdOrderByIdDesc(
            Long studentId
    );
}