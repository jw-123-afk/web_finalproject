package com.stiwk2124.project.springboot.backend.controller;

import com.stiwk2124.project.springboot.backend.exception.ResourceNotFoundException;
import com.stiwk2124.project.springboot.backend.model.User;
import com.stiwk2124.project.springboot.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/User")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: "+id));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Optional: check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        Optional<User> userOpt = userRepository.findByEmail(loginUser.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginUser.getPassword())) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user) {
        User updateUser = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User not exist with id: "+id));
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            updateUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            updateUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            updateUser.setPassword(user.getPassword());
        }

        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            updateUser.setPhone(user.getPhone());
        }
        userRepository.save(updateUser);

        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: "+id));

        userRepository.delete(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<?> getProfilePicture(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        byte[] image = user.getProfilePicture();
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new ByteArrayResource(image));
    }

    @PostMapping("/{id}/upload-picture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        try {
            user.setProfilePicture(file.getBytes());
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }

    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setProfilePicture(null); // Remove picture
        userRepository.save(user);

        return ResponseEntity.ok("Profile picture removed successfully");
    }
}
