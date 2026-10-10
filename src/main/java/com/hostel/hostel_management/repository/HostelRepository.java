package com.hostel.hostel_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.Hostel;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
}