package com.hostel.hostel_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}