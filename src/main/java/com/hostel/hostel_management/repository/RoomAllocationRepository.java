package com.hostel.hostel_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.RoomAllocation;

public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {
}