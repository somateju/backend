
package com.hostel.hostel_management.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hostel.hostel_management.entity.Preference;

@Service
public class MatchingService {

    // Calculate compatibility out of 100
    public int calculateCompatibility(
            Preference p1,
            Preference p2) {

        if (p1 == null || p2 == null) {
            return 0;
        }

        int score = 0;

        // Sleep schedule: 20 marks
        if (same(p1.getSleepSchedule(), p2.getSleepSchedule())) {
            score += 20;
        }

        // Study habits: 20 marks
        if (same(p1.getStudyHabit(), p2.getStudyHabit())) {
            score += 20;
        }

        // Cleanliness: 20 marks
        if (same(p1.getCleanliness(), p2.getCleanliness())) {
            score += 20;
        }

        // Food preference: 15 marks
        if (same(p1.getFoodPreference(), p2.getFoodPreference())) {
            score += 15;
        }

        // Smoking preference: 15 marks
        if (same(p1.getSmokingPreference(), p2.getSmokingPreference())) {
            score += 15;
        }

        // Room type: 10 marks
        if (same(p1.getRoomType(), p2.getRoomType())) {
            score += 10;
        }

        return score;
    }

    // Compare values safely, ignoring letter case and extra spaces
    private boolean same(String value1, String value2) {

        return value1 != null
                && value2 != null
                && value1.trim().equalsIgnoreCase(value2.trim());
    }

    // Check whether two students have the same gender
    private boolean sameGender(
            Preference p1,
            Preference p2) {

        if (p1.getStudent() == null || p2.getStudent() == null) {
            return false;
        }

        String gender1 = p1.getStudent().getGender();
        String gender2 = p2.getStudent().getGender();

        return same(gender1, gender2);
    }

    // Get all eligible roommates, ordered from highest to lowest score
    public List<Preference> findAllMatches(
            Preference target,
            List<Preference> allPreferences) {

        if (target == null || target.getStudent() == null
                || target.getStudent().getId() == null) {
            return List.of();
        }

        Long targetStudentId = target.getStudent().getId();

        return allPreferences.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getStudent() != null)
                .filter(p -> p.getStudent().getId() != null)
                // Exclude the logged-in student
                .filter(p -> !p.getStudent().getId().equals(targetStudentId))
                // Only students of the same gender
                .filter(p -> sameGender(target, p))
                // Only students with saved preferences
                .filter(p ->
                        p.getSleepSchedule() != null
                        && p.getStudyHabit() != null
                        && p.getCleanliness() != null
                        && p.getFoodPreference() != null
                        && p.getSmokingPreference() != null
                        && p.getRoomType() != null)
                // Highest compatibility first
                .sorted(
                        Comparator.comparingInt(
                                (Preference p) ->
                                        calculateCompatibility(target, p)
                        ).reversed()
                )
                .toList();
    }

    // Automatically find the highest-scoring roommate
    public Preference findBestMatch(
            Preference target,
            List<Preference> allPreferences) {

        List<Preference> matches =
                findAllMatches(target, allPreferences);

        if (matches.isEmpty()) {
            return null;
        }

        return matches.get(0);
    }
}
