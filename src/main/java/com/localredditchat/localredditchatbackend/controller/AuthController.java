package com.localredditchat.localredditchatbackend.controller;

import com.localredditchat.localredditchatbackend.model.User;
import com.localredditchat.localredditchatbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        return ResponseEntity.ok("User registered with username: " + createdUser.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean authenticated = userService.authenticate(user.getUsername(), user.getPassword());
        if(authenticated) {
            return ResponseEntity.ok("User logged in");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
