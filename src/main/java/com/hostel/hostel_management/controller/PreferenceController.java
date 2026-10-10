package com.hostel.hostel_management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostel.hostel_management.entity.Preference;
import com.hostel.hostel_management.repository.PreferenceRepository;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceRepository preferenceRepository;

    public PreferenceController(
            PreferenceRepository preferenceRepository) {

        this.preferenceRepository = preferenceRepository;
    }


    @PostMapping
    public Preference savePreference(
            @RequestBody Preference preference) {

        Long studentId =
                preference.getStudent().getId();


        /*
         * Check whether this student already
         * has preferences.
         */

        Preference existingPreference =
                preferenceRepository
                        .findByStudentId(studentId)
                        .orElse(null);


        /*
         * If preference already exists,
         * update the existing record.
         */

        if (existingPreference != null) {

            existingPreference.setSleepSchedule(
                    preference.getSleepSchedule()
            );

            existingPreference.setStudyHabit(
                    preference.getStudyHabit()
            );

            existingPreference.setCleanliness(
                    preference.getCleanliness()
            );

            existingPreference.setFoodPreference(
                    preference.getFoodPreference()
            );

            existingPreference.setSmokingPreference(
                    preference.getSmokingPreference()
            );

            existingPreference.setRoomType(
                    preference.getRoomType()
            );

            return preferenceRepository.save(
                    existingPreference
            );
        }


        /*
         * If no preference exists,
         * create a new one.
         */

        return preferenceRepository.save(preference);
    }


    @GetMapping
    public List<Preference> getAllPreferences() {

        return preferenceRepository.findAll();
    }
}