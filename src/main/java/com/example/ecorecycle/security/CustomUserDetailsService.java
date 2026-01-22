package com.example.ecorecycle.security;

import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.repository.BaseUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final BaseUserRepository baseUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BaseUser user = baseUserRepository.findByUsername(username)
                .or(() -> baseUserRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(mapRole(user.getRole()))
                .accountLocked(!user.getIsActive())
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRole(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
