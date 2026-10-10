
package com.hostel.hostel_management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Hostel;
import com.hostel.hostel_management.repository.HostelRepository;

@RestController
@RequestMapping("/api/hostels")
public class HostelController {

    private final HostelRepository hostelRepository;

    public HostelController(HostelRepository hostelRepository) {
        this.hostelRepository = hostelRepository;
    }

    @PostMapping
    public Hostel addHostel(@RequestBody Hostel hostel) {
        return hostelRepository.save(hostel);
    }

    @GetMapping
    public List<Hostel> getAllHostels() {
        return hostelRepository.findAll();
    }
}