package com.wex.purchase.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wex.purchase.exception.UserAlreadyExistsException;
import com.wex.purchase.model.User;
import com.wex.purchase.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNewUser(String username, String rawPassword) {
        //Check if user exists
    	if (userRepository.findByUsername(username).isPresent()) {
            // Throw custom exception
            throw new UserAlreadyExistsException("A user with the name '" + username + "' already exists.");
        }

        //Encode the password 
        String encodedPassword = passwordEncoder.encode(rawPassword);

        //Build and save
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .role("ROLE_USER")
                .enabled(true)
                .accountLocked(false)
                .build();

        return userRepository.save(user);
    }
}