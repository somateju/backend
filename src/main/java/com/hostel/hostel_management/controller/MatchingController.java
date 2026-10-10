package com.hostel.hostel_management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Preference;
import com.hostel.hostel_management.repository.PreferenceRepository;
import com.hostel.hostel_management.service.MatchingService;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final PreferenceRepository preferenceRepository;
    private final MatchingService matchingService;

    public MatchingController(
            PreferenceRepository preferenceRepository,
            MatchingService matchingService) {

        this.preferenceRepository = preferenceRepository;
        this.matchingService = matchingService;
    }

    @GetMapping("/{student1Id}/{student2Id}")
    public String calculateMatch(
            @PathVariable Long student1Id,
            @PathVariable Long student2Id) {

        Preference p1 =
                preferenceRepository.findAll()
                        .stream()
                        .filter(p ->
                                p.getStudent()
                                        .getId()
                                        .equals(student1Id))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Preference not found for Student 1"));

        Preference p2 =
                preferenceRepository.findAll()
                        .stream()
                        .filter(p ->
                                p.getStudent()
                                        .getId()
                                        .equals(student2Id))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Preference not found for Student 2"));

        // Same gender check
        if (!p1.getStudent()
                .getGender()
                .equalsIgnoreCase(
                        p2.getStudent().getGender())) {

            return "Students must be of the same gender";
        }

        int score =
                matchingService.calculateCompatibility(
                        p1,
                        p2);

        return "Compatibility: "
                + score
                + "%";
    }


    @GetMapping("/best/{studentId}")
    public String findBestMatch(
            @PathVariable Long studentId) {

        List<Preference> allPreferences =
                preferenceRepository.findAll();

        Preference target =
                allPreferences.stream()
                        .filter(p ->
                                p.getStudent()
                                        .getId()
                                        .equals(studentId))
                        .findFirst()
                        .orElse(null);

        if (target == null) {

            return "Please submit your preferences first";
        }

        Preference bestMatch =
                matchingService.findBestMatch(
                        target,
                        allPreferences);

        if (bestMatch == null) {

            return "No suitable roommate found";
        }

        int score =
                matchingService.calculateCompatibility(
                        target,
                        bestMatch);

        return "Best Match: "
                + bestMatch.getStudent().getName()
                + " | Student ID: "
                + bestMatch.getStudent().getId()
                + " | Compatibility: "
                + score
                + "%";
    }
}