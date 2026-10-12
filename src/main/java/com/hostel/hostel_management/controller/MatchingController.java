
package com.hostel.hostel_management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
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

    // Calculate compatibility between two specific students
    @GetMapping("/{student1Id}/{student2Id}")
    public ResponseEntity<?> calculateMatch(
            @PathVariable Long student1Id,
            @PathVariable Long student2Id) {

        List<Preference> allPreferences =
                preferenceRepository.findAll();

        Preference p1 = findPreference(allPreferences, student1Id);
        Preference p2 = findPreference(allPreferences, student2Id);

        if (p1 == null) {
            return ResponseEntity.badRequest()
                    .body("First student has not submitted preferences.");
        }

        if (p2 == null) {
            return ResponseEntity.badRequest()
                    .body("Second student has not submitted preferences.");
        }

        if (p1.getStudent() == null || p2.getStudent() == null
                || p1.getStudent().getGender() == null
                || p2.getStudent().getGender() == null) {
            return ResponseEntity.badRequest()
                    .body("Gender information is missing.");
        }

        if (!p1.getStudent().getGender()
                .equalsIgnoreCase(p2.getStudent().getGender())) {
            return ResponseEntity.badRequest()
                    .body("Students must be of the same gender.");
        }

        int score = matchingService.calculateCompatibility(p1, p2);

        Map<String, Object> result = new HashMap<>();
        result.put("student1Id", student1Id);
        result.put("student2Id", student2Id);
        result.put("compatibility", score);

        return ResponseEntity.ok(result);
    }

    // Get the best roommate automatically
    @GetMapping("/best/{studentId}")
    public ResponseEntity<?> findBestMatch(
            @PathVariable Long studentId) {

        List<Preference> allPreferences =
                preferenceRepository.findAll();

        Preference target =
                findPreference(allPreferences, studentId);

        if (target == null) {
            return ResponseEntity.badRequest()
                    .body("Please submit your preferences first.");
        }

        List<Preference> matches =
                matchingService.findAllMatches(
                        target,
                        allPreferences
                );

        if (matches.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "No eligible roommate found. Other students may need to submit their preferences."
                    )
            );
        }

        Preference best = matches.get(0);

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", best.getStudent().getId());
        result.put("name", best.getStudent().getName());
        result.put(
                "compatibility",
                matchingService.calculateCompatibility(target, best)
        );

        return ResponseEntity.ok(result);
    }

    // Get all eligible roommates, ranked by compatibility
    @GetMapping("/all/{studentId}")
    public ResponseEntity<?> findAllMatches(
            @PathVariable Long studentId) {

        List<Preference> allPreferences =
                preferenceRepository.findAll();

        Preference target =
                findPreference(allPreferences, studentId);

        if (target == null) {
            return ResponseEntity.badRequest()
                    .body("Please submit your preferences first.");
        }

        List<Preference> matches =
                matchingService.findAllMatches(
                        target,
                        allPreferences
                );

        List<Map<String, Object>> results =
                matches.stream()
                        .map(preference -> {

                            Map<String, Object> item =
                                    new HashMap<>();

                            item.put(
                                    "studentId",
                                    preference.getStudent().getId()
                            );

                            item.put(
                                    "name",
                                    preference.getStudent().getName()
                            );

                            item.put(
                                    "branch",
                                    preference.getStudent().getBranch()
                            );

                            item.put(
                                    "year",
                                    preference.getStudent().getYear()
                            );

                            item.put(
                                    "compatibility",
                                    matchingService.calculateCompatibility(
                                            target,
                                            preference
                                    )
                            );

                            return item;
                        })
                        .toList();

        return ResponseEntity.ok(results);
    }

    private Preference findPreference(
            List<Preference> preferences,
            Long studentId) {

        return preferences.stream()
                .filter(p -> p.getStudent() != null)
                .filter(p -> p.getStudent().getId() != null)
                .filter(p -> p.getStudent().getId().equals(studentId))
                .findFirst()
                .orElse(null);
    }
}
