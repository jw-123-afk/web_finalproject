package com.stiwk2124.project.springboot.backend.controller;

import com.stiwk2124.project.springboot.backend.exception.ResourceNotFoundException;
import com.stiwk2124.project.springboot.backend.model.Achievement;
import com.stiwk2124.project.springboot.backend.model.User;
import com.stiwk2124.project.springboot.backend.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/Achievement")
public class AchievementController {

    @Autowired
    private AchievementRepository achievementRepository;

    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @GetMapping("/filterBy/{userId}")
    public List<Achievement> getAchievementsByUserId(@PathVariable Long userId) {
        return achievementRepository.findByUserId(userId);
    }

    @PostMapping
    public Achievement createAchievement(@RequestBody Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteAchievement(@PathVariable long id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: "+id));

        achievementRepository.delete(achievement);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
