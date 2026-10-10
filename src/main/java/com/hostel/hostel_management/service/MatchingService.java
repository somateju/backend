package com.hostel.hostel_management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hostel.hostel_management.entity.Preference;

@Service
public class MatchingService {

    public int calculateCompatibility(
            Preference p1,
            Preference p2) {

        int score = 0;

        if (p1.getSleepSchedule()
                .equalsIgnoreCase(
                        p2.getSleepSchedule())) {
            score += 20;
        }

        if (p1.getStudyHabit()
                .equalsIgnoreCase(
                        p2.getStudyHabit())) {
            score += 20;
        }

        if (p1.getCleanliness()
                .equalsIgnoreCase(
                        p2.getCleanliness())) {
            score += 20;
        }

        if (p1.getFoodPreference()
                .equalsIgnoreCase(
                        p2.getFoodPreference())) {
            score += 15;
        }

        if (p1.getSmokingPreference()
                .equalsIgnoreCase(
                        p2.getSmokingPreference())) {
            score += 15;
        }

        if (p1.getRoomType()
                .equalsIgnoreCase(
                        p2.getRoomType())) {
            score += 10;
        }

        return score;
    }

    public Preference findBestMatch(
            Preference target,
            List<Preference> allPreferences) {

        Preference bestMatch = null;

        int highestScore = -1;

        for (Preference preference : allPreferences) {

            // Do not match the student with themselves
            if (preference.getStudent()
                    .getId()
                    .equals(
                            target.getStudent().getId())) {
                continue;
            }

            // Only match students of the same gender
            if (!preference.getStudent()
                    .getGender()
                    .equalsIgnoreCase(
                            target.getStudent().getGender())) {
                continue;
            }

            int score =
                    calculateCompatibility(
                            target,
                            preference
                    );

            if (score > highestScore) {

                highestScore = score;

                bestMatch = preference;
            }
        }

        return bestMatch;
    }
}