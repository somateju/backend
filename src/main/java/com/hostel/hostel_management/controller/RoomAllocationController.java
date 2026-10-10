
package com.hostel.hostel_management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Room;
import com.hostel.hostel_management.entity.RoomAllocation;
import com.hostel.hostel_management.entity.Student;
import com.hostel.hostel_management.repository.RoomAllocationRepository;
import com.hostel.hostel_management.repository.RoomRepository;
import com.hostel.hostel_management.repository.StudentRepository;

@RestController
@RequestMapping("/api/allocations")
public class RoomAllocationController {

    private final RoomAllocationRepository allocationRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public RoomAllocationController(
            RoomAllocationRepository allocationRepository,
            StudentRepository studentRepository,
            RoomRepository roomRepository) {

        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping("/{studentId}/{roomId}")
    public String allocateRoom(
            @PathVariable Long studentId,
            @PathVariable Long roomId) {

        // Find student
        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"
                        ));

        // Check if student already has a room
        boolean alreadyAllocated =
                allocationRepository
                        .findAll()
                        .stream()
                        .anyMatch(allocation ->
                                allocation.getStudent()
                                        .getId()
                                        .equals(studentId)
                        );

        if (alreadyAllocated) {
            return "Student already has a room allocated";
        }

        // Find room
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Room not found"
                        ));

        // Check room capacity
        if (room.getOccupied() >= room.getCapacity()) {
            return "Room is full";
        }

        // Get hostel name
        String hostelName =
                room.getHostel().getName();

        // Check gender and hostel type
        boolean isFemale =
                student.getGender()
                        .equalsIgnoreCase("Female");

        boolean isMale =
                student.getGender()
                        .equalsIgnoreCase("Male");

        boolean isGirlsHostel =
                hostelName
                        .toLowerCase()
                        .contains("girls");

        boolean isBoysHostel =
                hostelName
                        .toLowerCase()
                        .contains("boys");

        // Female → Girls Hostel
        if (isFemale && !isGirlsHostel) {
            return "Female students can only be allocated to Girls Hostels";
        }

        // Male → Boys Hostel
        if (isMale && !isBoysHostel) {
            return "Male students can only be allocated to Boys Hostels";
        }

        // Create allocation
        RoomAllocation allocation =
                new RoomAllocation();

        allocation.setStudent(student);
        allocation.setRoom(room);
        allocation.setAllocationDate(
                LocalDate.now()
        );

        // Increase occupied count
        room.setOccupied(
                room.getOccupied() + 1
        );

        roomRepository.save(room);
        allocationRepository.save(allocation);

        return "Room allocated successfully";
    }

    @GetMapping
    public List<RoomAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }
}