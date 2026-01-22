package com.example.ecorecycle.service;

import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.entity.User;
import com.example.ecorecycle.entity.UserType;
import com.example.ecorecycle.entity.Recycler;
import com.example.ecorecycle.repository.UserRepository;
import com.example.ecorecycle.repository.RecyclerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RecyclerRepository recyclerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String name, String email, String username, String rawPassword, UserType userType) {
        User user = User.builder()
                .name(name)
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .userType(userType)
                .role(Role.USER)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public Recycler registerRecycler(String name, String email, String rawPassword, String serviceArea) {
        Recycler recycler = Recycler.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .serviceArea(serviceArea)
                .isActive(true)
                .build();
        return recyclerRepository.save(recycler);
    }
}

