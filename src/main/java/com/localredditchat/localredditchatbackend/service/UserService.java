package com.localredditchat.localredditchatbackend.service;

import com.localredditchat.localredditchatbackend.model.User;
import com.localredditchat.localredditchatbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // For demo, no password encoding yet; just save
        return userRepository.save(user);
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            return userOpt.get().getPassword().equals(password);
        }
        return false;
    }
}
