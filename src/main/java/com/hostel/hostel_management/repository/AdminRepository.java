package com.hostel.hostel_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}