package com.hostel.hostel_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.hostel_management.entity.Preference;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Optional<Preference> findByStudentId(Long studentId);

}